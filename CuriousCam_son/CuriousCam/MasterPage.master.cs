using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Configuration;

public partial class MasterPage: System.Web.UI.MasterPage
{
    //Toolbox
    Functions func = new Functions();

    protected void Page_Load(object sender, EventArgs e)
    {
         //If not logged in
        if (!func.loggedIn())
        {
            Response.Redirect("/Login.aspx");
        }
        else
        {
            userImage.ImageUrl = "photos/" + Session["photo"];
            userName.Text = Session["name"] + " " + Session["surname"] + "<small> <small>(" + func.getUserTypeName() + ")</small></small>";
            
        }

        //Company name
        bottomLabel.Text = ConfigurationManager.AppSettings["CompanyName"];  
    }
      
}
