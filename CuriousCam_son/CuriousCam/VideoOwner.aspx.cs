using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class VideoOwner : System.Web.UI.Page
{
     //Toolbox
    Functions func = new Functions();

    static int userID;

    protected void Page_Load(object sender, EventArgs e)
    {           
        if (!IsPostBack)
        {
            if (Request.QueryString["no"] != null) {
                userID = Convert.ToInt32(Request.QueryString["no"]);
                LoadData(userID);
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
            ownerName.InnerHtml = c.Name + " "+  c.Surname;
            emailLabel.Text = c.Email;
            facultyLabel.Text = func.getFacultyName(c.FacultyID);
            departmentLabel.Text = func.getDepartmentName(c.DepartmentID);

            if (c.Photo != "")
            {
                userImage.ImageUrl = "photos/" + c.Photo;
            }

            //Query
            Videos[] v = (from y in db.Videos
                        where y.UserID == userID
                        select y).ToArray();

            string html = "<ul class='list-group'>";
            
            for (int i = 0; i < v.Length; i++)
            {

                html += "<li class='list-group-item'>";
                html += "<h5><a href='VideoShow.aspx?no=" + v[i].VideoID.ToString() + "'>" +
                       "<b>" + v[i].Title + "</b></a> <small><i>(" + v[i].UploadDate.ToShortDateString() + ")</i></small></h5>";
                html += "</li>";
            }

            html += "</ul>";

            videoList.InnerHtml = html;
        }
    }

}   