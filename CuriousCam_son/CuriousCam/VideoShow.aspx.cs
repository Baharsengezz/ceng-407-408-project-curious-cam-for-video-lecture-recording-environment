using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class VideoShow: System.Web.UI.Page
{
     //Toolbox
    Functions func = new Functions();

    protected void Page_Load(object sender, EventArgs e)
    {
        if (!IsPostBack)
        {
            int videoID = Convert.ToInt32(Request.QueryString["no"]);

            CuriousCamEntities db = new CuriousCamEntities();

            Videos v = (from x in db.Videos
                          where x.VideoID == videoID
                          select x).SingleOrDefault();
            if (v != null)
            {
                string Path = "videos/" + v.Path;

                videoTitle.InnerHtml = v.Title;

                videoSrc1.Attributes["src"] = Path;
                videoSrc2.Attributes["src"] = Path;
                videoSrc3.Attributes["src"] = Path;
                videoSrc4.Attributes["src"] = Path;

                ownerLabel.Text = func.getUserName(v.UserID);
                ownerLink.NavigateUrl = "VideoOwner.aspx?no=" + v.UserID.ToString();
                topicLabel.Text = func.getTopicName(v.TopicID);
                subTopicLabel.Text = func.getSubTopicName(v.SubTopicID);
                dateLabel.Text = v.UploadDate.ToShortDateString();
            }
            else
            {
               videoTitle.InnerHtml="Wrong or no video number ...";
            }
        }
    }

}