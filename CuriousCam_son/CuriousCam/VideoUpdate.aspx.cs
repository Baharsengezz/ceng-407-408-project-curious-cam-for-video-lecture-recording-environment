using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.IO;

public partial class VideoUpdate : System.Web.UI.Page
{
     //Toolbox
    Functions func = new Functions();

    static int userID;
    static int videoID;

    protected void Page_Load(object sender, EventArgs e)
    {
        if (func.getUserType()==2 )
            Response.Redirect("/");

         userID = func.getUserId();
         videoID = Convert.ToInt32(Request.QueryString["no"]);

        if (!IsPostBack)
        {
            loadData(videoID);
        }
    }

    void showMessage(String msg)
    {
        messageLabel.Visible = true;
        messageLabel.Text = msg;
    }

    void loadData(int videoID)
    {
        CuriousCamEntities db = new CuriousCamEntities(); ;

        //Query
        Videos c = (from x in db.Videos
                   where x.VideoID == videoID
                   select x).SingleOrDefault();

        //If a matching record has been found
        if (c != null)
        {
            if (c.UserID != userID && !func.isAdmin())
                Response.Redirect("/");

            titleText.Text = c.Title;
            
            videoSrc1.Attributes["src"] = "videos/" + c.Path;
            videoSrc2.Attributes["src"] = "videos/" + c.Path;
            videoSrc3.Attributes["src"] = "videos/" + c.Path;
            

            topicDDList.SelectedValue = c.TopicID.ToString();
            loadSubTopics(c.TopicID);

            uploadDateLabel.Text = c.UploadDate.ToShortDateString();

            subTopicDDList.SelectedValue = c.SubTopicID.ToString();
        }
        else
        {
            showMessage("Video Update: Wrong Video ID:" + videoID.ToString());
        }
    }

    protected void updateButton_Click(object sender, EventArgs e)
    {
        //Database
        CuriousCamEntities db = new CuriousCamEntities();

        //Table
        Videos c = (from x in db.Videos
                   where x.VideoID == videoID
                   select x).SingleOrDefault();

        if (c != null)
        {

            c.Title = titleText.Text;
            c.TopicID = Convert.ToInt32(topicDDList.SelectedValue);
            c.SubTopicID = Convert.ToInt32(subTopicDDList.SelectedValue);

            try
            {

                if (FileUpload.FileName != "")
                {
                    if (func.isVideo(FileUpload.FileName) == false)
                    {
                        showMessage("The file you selected must be either of these video formats: mp4, webm, ogv or flv");
                        return;
                    }


                    if (File.Exists(Server.MapPath("videos/") + c.Path))
                    {
                        File.Delete(Server.MapPath("videos/") + c.Path);
                    }
                    
                    string newFileName = func.getUserVideoFileName(c.UserID, c.VideoID, FileUpload.FileName);
                    FileUpload.SaveAs(Server.MapPath("/videos/") + newFileName);

                    c.Path = newFileName;
                    
                    string vs = "videos/" + c.Path + "?time=" + DateTime.Now;

                    videoSrc1.Attributes["src"] = vs;
                    videoSrc2.Attributes["src"] = vs;
                    videoSrc3.Attributes["src"] = vs;
                    

                    c.UploadDate = DateTime.Now;

                }

                db.SaveChanges();

                showMessage("Your video has been successfully apdated...");


            }
            catch (Exception ex)
            {
                showMessage("Error:" + ex.ToString());
            }
           
        }
        else
        {
            showMessage("Video Update: Wrong Video ID:" + videoID.ToString());
        }
    }

    protected void loadSubTopics(int topicID)
    {
        subTopicDDList.Items.Clear();

        CuriousCamEntities db = new CuriousCamEntities();

        SubTopics [] st = (from x in db.SubTopics
                            where x.MainTopicID == topicID
                            select x).ToArray();

        if (st != null)
        {


            for (int i = 0; i <st.Length; i++)
            {
                ListItem l = new ListItem(st[i].SubTopic, Convert.ToString(st[i].SubTopicID));

                subTopicDDList.Items.Add(l);
            }
        }

    }

    protected void topicDDList_TextChanged(object sender, EventArgs e)
    {
          loadSubTopics(Convert.ToInt32(topicDDList.SelectedValue));
    }

    protected void deleteButton_Click(object sender, EventArgs e)
    {
        //Database
        CuriousCamEntities db = new CuriousCamEntities();

        //Table
        Videos c = (from x in db.Videos
                    where x.VideoID == videoID
                    select x).SingleOrDefault();
        if (c!=null){
            db.Videos.Remove(c);
            db.SaveChanges();

            string fileFullPath = Server.MapPath("/videos/") + c.Path;

            if (File.Exists(fileFullPath))
            {
                File.Delete(fileFullPath);
            }
        }
        else {
            showMessage("Video Delete: Wrong Video ID:" + videoID.ToString());
        }
            

    }
}