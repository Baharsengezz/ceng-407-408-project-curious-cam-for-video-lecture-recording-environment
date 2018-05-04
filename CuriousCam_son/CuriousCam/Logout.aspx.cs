using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class Default2 : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        //End the current session and reset all keys.
         nameLabel.Text = Session["name"]+ " " + Session["surname"];
       
        Session.Clear(); 
        Session.Abandon();

        Response.Cookies["email"].Expires = DateTime.Now.AddDays(-1);
        Response.Cookies["password"].Expires = DateTime.Now.AddDays(-1);
    }
}