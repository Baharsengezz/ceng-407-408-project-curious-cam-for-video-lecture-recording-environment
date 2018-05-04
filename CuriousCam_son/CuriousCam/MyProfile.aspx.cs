using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.IO;

public partial class MyProfile: System.Web.UI.Page
{
     //Toolbox
    Functions func = new Functions();

    static int userID;

    protected void Page_Load(object sender, EventArgs e)
    {
        userID = func.getUserId();

        if (!IsPostBack)
        {
            
            LoadData(userID);

            if (!func.isAdmin())
            {
                userTypeDDList.Enabled = false;
                facultyDDList.Enabled = false;
                departmentDDList.Enabled = false;
            }
        }
    }

    void LoadData(int userID)
    {
        CuriousCamEntities db = new CuriousCamEntities(); ;

        //Query
        Users c = (from x in db.Users
                     where x.UserID == userID
                     select x).SingleOrDefault();

        //If a matching record has been found
        if (c != null)
        {
             //New record
            nameText.Text = c.Name;
            surnameText.Text = c.Surname;

            emailText.Text = c.Email;

            phoneText.Text = c.Phone;
        
            userTypeDDList.SelectedValue = c.UserType.ToString();
            facultyDDList.SelectedValue=c.FacultyID.ToString();
            
            loadDepartments(c.FacultyID);

            departmentDDList.SelectedValue = c.DepartmentID.ToString();

            if (c.Photo != "")
            {
                userImage.ImageUrl = "photos/" + c.Photo;
            }
        }
    }

    void showMessage(String msg)
    {   
        messageLabel.Visible = true;
        messageLabel.Text = msg;
    }

    protected void updateButton_Click(object sender, EventArgs e)
    {
        //Database
        CuriousCamEntities db = new CuriousCamEntities();

        //Table
        Users c = (from x in db.Users
                     where x.UserID == userID
                     select x).SingleOrDefault();

        if (c != null) {
            //New record
            c.Name = nameText.Text;
            c.Surname = surnameText.Text;

            c.Email = emailText.Text;
            c.Phone  = phoneText.Text;
        
            c.UserType = Convert.ToInt32(userTypeDDList.SelectedValue);
            c.FacultyID = Convert.ToInt32(facultyDDList.SelectedValue);
            c.DepartmentID = Convert.ToInt32(departmentDDList.SelectedValue);
   
            try{   

                if (FileUpload.FileName != "")
                {
                    if (func.isImage(FileUpload.FileName) == false)
                    {
                        showMessage("The file you selected must be one these picture formats: gif, jpg or png");
                        return;
                    }

                    string newFileName = func.setUserPhoto(emailText.Text, FileUpload.FileName);

                    FileUpload.SaveAs(Server.MapPath("/photos/") + newFileName);
                    userImage.ImageUrl = "photos/" + newFileName + "?time=" + DateTime.Now;

                    c.Photo = newFileName;
                }

                db.SaveChanges();

                showMessage("Your information has been successfully updated.");

                Session["name"] = c.Name;
                Session["surname"] = c.Surname;
                Session["photo"] = c.Photo;

            }
            catch (Exception ex)
            {
                showMessage("An error occured during update. If you try to change your email. Please try another email.");
            }

        }
        else
        {
            showMessage("User cannot be found in th database:" + userID);
        }
    }

    protected void facultyDDlist_OnChange(object sender, EventArgs e)
    {
        loadDepartments(Convert.ToInt32(facultyDDList.SelectedValue));
    }

    protected void loadDepartments(int facultyID)
    {
        departmentDDList.Items.Clear();

        CuriousCamEntities db = new CuriousCamEntities();

        Departments[] dp = (from x in db.Departments
                            where x.FacultyID == facultyID
                            select x).ToArray();
        if (dp != null)
        {


            for (int i = 0; i < dp.Length; i++)
            {
                ListItem l = new ListItem(dp[i].Name, Convert.ToString(dp[i].DepartmentID));

                departmentDDList.Items.Add(l);
            }
        }

    }
}   