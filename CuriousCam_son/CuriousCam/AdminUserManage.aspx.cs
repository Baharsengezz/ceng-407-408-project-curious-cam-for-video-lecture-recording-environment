using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.IO;

public partial class AdminUserManage : System.Web.UI.Page
{
     //Toolbox
    Functions func = new Functions();

    static int userID;

    protected void Page_Load(object sender, EventArgs e)
    {
        if (!func.isAdmin())
            Response.Redirect("/");


        if (!IsPostBack)
        {
            userID = Convert.ToInt32(Request.QueryString["no"]);
            LoadData(userID);
        }
       
    }

    protected void deleteButton_Click(object sender, EventArgs e)
    {
        if (func.removeUser(userID))
        {
            showMessage(func.stateMessage);
            updateButton.Enabled = false;
            approveButton.Enabled = false;
            deleteButton.Enabled = false;
            deactivateButton.Enabled = false;
            adminButton.Enabled = false;

        }
        else
        {
            showMessage(func.errorMessage);
        }
    }

    void LoadData(int userID)
    {
        CuriousCamEntities db = new CuriousCamEntities(); ;

        //Query
        Users c = (from x in db.Users
                     where x.UserID == userID
                     select x).SingleOrDefault();

        
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

            approveButton.Enabled = !c.IsApproved;
            if (c.IsActive)
                deactivateButton.Text = "Deactivate";
            else
                deactivateButton.Text = "Activate";

            if (c.IsAdmin)
                adminButton.Text = "Cancel Admin";
            else
                adminButton.Text = "Make Admin";

            if (c.UserType == 1)
            {
                userHomeLink1.NavigateUrl = "VideoOwner.aspx?no=" + c.UserID.ToString();
                userHomeLink2.NavigateUrl = "VideoOwner.aspx?no=" + c.UserID.ToString();
            }
            else
            {
                userHomeLink2.Visible = false;
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

                    string newFileName = func.setUserPhoto(emailText.Text, FileUpload.FileName);

                    FileUpload.SaveAs(Server.MapPath("/photos/") + newFileName);
                    userImage.ImageUrl = "photos/" + newFileName;

                    c.Photo = newFileName;
                }

                db.SaveChanges();

                showMessage("User information has been successfully updated.");

                if (userID == func.getUserId())
                {
                    Session["name"] = c.Name;
                    Session["surname"] = c.Surname;
                    Session["photo"] = FileUpload.FileName;
                }

            }
            catch (Exception ex)
            {
                showMessage("An error occured:" + ex.ToString());
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
    protected void approveButton_Click(object sender, EventArgs e)
    {
        //Database
        CuriousCamEntities db = new CuriousCamEntities();

        //Table
        Users c = (from x in db.Users
                   where x.UserID == userID
                   select x).SingleOrDefault();

        //New values

        if (c != null)
        {
            c.IsApproved = true;

            try
            {
                db.SaveChanges();

                showMessage("User has been approved succesfully...");

                approveButton.Enabled = false;
            }
            catch (Exception ex)
            {
                showMessage("An error occured:" + ex.ToString());
            }
        }
        else
        {
            showMessage("There is a problem with User ID:" + userID);
        }

    }
    protected void deactivateButton_Click(object sender, EventArgs e)
    {
        //Database
        CuriousCamEntities db = new CuriousCamEntities();

        //Table
        Users c = (from x in db.Users
                   where x.UserID == userID
                   select x).SingleOrDefault();

        //New values

        if (c != null)
        {
            c.IsActive = !c.IsActive;

            try
            {
                db.SaveChanges();

                showMessage("User has been " + deactivateButton.Text + "d succesfully...");

                if (c.IsActive)
                    deactivateButton.Text = "Deactivate";
                else
                    deactivateButton.Text = "Activate";
               
                
            }
            catch (Exception ex)
            {
                showMessage("An error occured:" + ex.ToString());
            }
        }
        else
        {
            showMessage("There is a problem with User ID:" + userID);
        }

    }

    protected void adminButton_Click(object sender, EventArgs e)
    {
        //Database
        CuriousCamEntities db = new CuriousCamEntities();

        //Table
        Users c = (from x in db.Users
                   where x.UserID == userID
                   select x).SingleOrDefault();

        //New values

        if (c != null)
        {
            c.IsAdmin = !c.IsAdmin;

            try
            {
                db.SaveChanges();

                if (c.IsAdmin)
                    adminButton.Text = "Cancel Admin";
                else
                    adminButton.Text = "Make Admin";

                showMessage("User has been " + adminButton.Text + "ed succesfully...");

            }
            catch (Exception ex)
            {
                showMessage("An error occured:" + ex.ToString());
            }
        }
        else
        {
            showMessage("There is a problem with User ID:" + userID);
        }
    }
}   