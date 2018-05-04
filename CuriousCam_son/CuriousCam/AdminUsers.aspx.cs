using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class AdminUsers : System.Web.UI.Page
{
     //Toolbox
    Functions func = new Functions();

    protected void Page_Load(object sender, EventArgs e)
    {
        if (!func.isAdmin())
            Response.Redirect("/");

        if (!IsPostBack)
        {
            string cat = Request.QueryString["cat"];
            listUsers(cat);
          
        }
    }

    public void listUsers(string cat)
    {
        CuriousCamEntities db = new CuriousCamEntities();

        Users[] b;

        if (cat == "apr")
        {
            b = (from x in db.Users where x.IsApproved == false orderby x.IsActive descending select x).ToArray();
            pageTitle.Text = "Users Waiting for Approvement";
        }
        else if (cat == "ins")
        {
            b = (from x in db.Users where x.UserType == 1 orderby x.IsActive descending select x).ToArray();
            pageTitle.Text = "Instructors";
        }
        else if (cat == "std")
        {
            b = (from x in db.Users where x.UserType == 2 orderby x.IsActive descending select x).ToArray();
            pageTitle.Text = "Students";
        }
        else if (cat == "oth")
        {
            b = (from x in db.Users where x.UserType == 0 orderby x.IsActive descending select x).ToArray();
            pageTitle.Text = "Other University Staff";
        }
        else
        {
            b = (from x in db.Users orderby x.IsActive descending select x).ToArray();
            pageTitle.Text = "All Users";
        }


   
        

        string html = "No user to list.";
        bool putLine=true;

        if (b != null)
        {
            if (b.Length > 0)
            {
                html = "<ul class='list-group'>";

                for (int i = 0; i < b.Length; i++)
                {
                    if (b[i].IsActive == false && putLine)
                    {
                        html += "<br/><h5 class='text-danger'>Deactivated Users:</h5>";
                        putLine = false;
                    }

                    html += "<li class='list-group-item'>";
                    html += "<a href='AdminUserManage.aspx?no=" + b[i].UserID + "'>";
                    html += b[i].Name + " " + b[i].Surname + "</a> - <small>" + func.getUserTypeName(b[i].UserType) + "</small>";
                    html += "</li>";

                    

                }

                if (putLine)
                {
                    html += "<br/><h5 class='text-danger'>Deactivated Users:</h5><li class='list-group-item'>No inactive user.</li>";
                }

                html += "</ul>";
            }

            
        }

        userList.InnerHtml = html;
        

    }

}