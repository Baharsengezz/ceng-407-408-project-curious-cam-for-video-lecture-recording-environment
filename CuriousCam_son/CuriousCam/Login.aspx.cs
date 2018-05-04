using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class Login : System.Web.UI.Page
{
    //User type 
    int usertype=-1;

    Functions func = new Functions();

    protected void Page_Load(object sender, EventArgs e)
    {

        if (!IsPostBack)
        {
            
            //Kullanıcı login olmuş mu?
            if (func.loggedIn())
            {
                Response.Redirect("/");
                return;
            }
            else
            {
                
                func.setSessionVar(-1, false, "", "", "", -1, "", false);
                usertype = -1;

                
                if (Request.Cookies["email"] != null)
                {
                    login(Request.Cookies["email"].Value, Request.Cookies["password"].Value);
                }
            }
        }
    }

    protected void loginButton_Click(object sender, EventArgs e)
    {
        login(emailText.Text, passwordText.Text);
    }

    void login(string email, string password)
    {
        
        
        func.setSessionVar(-1, false, "", "", "", -1,"",false);
     
        CuriousCamEntities db = new CuriousCamEntities();
            
        Users  p = (   from x in db.Users
                        where x.Email == email &&
                        x.Password == password
                        select x).SingleOrDefault();

        if (p == null)
        {
            showMessage("Please check your user name and password!");
        }
        else
        {
            Boolean state = p.IsActive && p.IsApproved;
            func.setSessionVar(p.UserType, p.IsAdmin, p.Email, p.Name, p.Surname, p.UserID, p.Photo, state);

             
            if (rememberCheckBox.Checked)
            {
                Response.Cookies["email"].Value = p.Email;
                Response.Cookies["email"].Expires = DateTime.Now.AddDays(7);

                Response.Cookies["password"].Value = p.Password;
                Response.Cookies["password"].Expires = DateTime.Now.AddDays(7);
            }
            else
            {
                Response.Cookies["email"].Expires = DateTime.Now.AddDays(-1);
                Response.Cookies["password"].Expires = DateTime.Now.AddDays(-1);
 
            }

            if (state)
            {
                showMessage("Welcome " + p.Name + " " + p.Surname);
                Response.Redirect("/");
            }
            else
            {
                if (!p.IsActive)
                    showMessage(" Dear " + p.Name + " " + p.Surname + " your account is disabled. Please contact your administrator.");
                else
                    showMessage(" Dear " + p.Name + " " + p.Surname + " your membership is still in the approval process.");
            }
        

        }

    }

    void showMessage(String msg)
    {
        
        messageLabel.Visible = true;
        messageLabel.Text = msg;
    }

}