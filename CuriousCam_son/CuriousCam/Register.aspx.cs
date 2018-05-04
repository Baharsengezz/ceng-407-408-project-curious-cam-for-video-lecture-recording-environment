using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.IO;

public partial class Register: System.Web.UI.Page
{
    Functions func = new Functions();
    
    protected void Page_Load(object sender, EventArgs e)
    {
        if (!IsPostBack)
        {
           loadDepartments(1);
        }
    }


    void showMessage(String msg)
    {
        messageLabel.Visible = true;
        messageLabel.Text = msg;
    }

    protected void btnRegister_Click(object sender, EventArgs e)
    {
        //Database
        CuriousCamEntities db = new CuriousCamEntities();

        //Table
        Users newRec = new Users();

        //New record
        newRec.Name = nameText.Text;
        newRec.Surname = surnameText.Text;

        newRec.Email = emailText.Text;
        newRec.Password = password1Text.Text;

        newRec.Phone  = phoneText.Text;
        
        newRec.UserType = Convert.ToInt32(userTypeDDList.SelectedValue);
        newRec.FacultyID = Convert.ToInt32(facultyDDList.SelectedValue);
        newRec.DepartmentID = Convert.ToInt32(departmentDDList.SelectedValue);

        //Active but not approved
        newRec.IsActive = true;

        if (FileUpload.FileName != "")
        {
            if (func.isImage(FileUpload.FileName) == false)
            {
                showMessage("The file you selected must be one these picture formats: gif, jpg or png");
                return;
            }

            newRec.Photo = FileUpload.FileName;
        }
        else
        {
            showMessage("Please select a picture of yours.");
        }
        try
        {
           
            db.Users.Add(newRec);
            db.SaveChanges();

            
            string newFileName = func.setUserPhoto(emailText.Text, FileUpload.FileName);  
 
            FileUpload.SaveAs(Server.MapPath("/photos/") + newFileName);
            Image.ImageUrl = "photos/" + newFileName;                
            

            submitButton.Enabled = false;

            showMessage("Your information has been successfully saved. You can log-in after you are approved.");

            
        }
        catch (Exception ex)
        {
            showMessage("Please try a different username." + ex.ToString());
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