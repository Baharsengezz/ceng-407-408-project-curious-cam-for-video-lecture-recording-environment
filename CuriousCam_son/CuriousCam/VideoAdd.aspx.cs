using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class VideoAdd : System.Web.UI.Page
{
     //Toolbox
    Functions func = new Functions();

    static int userID;

    protected void Page_Load(object sender, EventArgs e)
    {
        if (func.getUserType() != 1)
            Response.Redirect("/");

        userID = func.getUserId();

        if (!IsPostBack)
        {
            loadSubTopics(1);
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
        Videos newRec = new Videos();

        //New record
        newRec.Title = titleText.Text;
        newRec.UserID = userID;
        
        newRec.TopicID= Convert.ToInt32(topicDDList.SelectedValue);
        newRec.SubTopicID = Convert.ToInt32(subTopicDDList.SelectedValue);
        newRec.UploadDate = DateTime.Now.Date;

        if (FileUpload.FileName != "")
        {

            if (func.isVideo(FileUpload.FileName) == false)
            {
                showMessage("The file you selected must be either of these video formats: mp4, webm or ogv.");
                return;
            }

            newRec.Path = FileUpload.FileName;
        }
        else
        {
            showMessage("You should select a video file...");
            return;
        }

        try
        {
            db.Videos.Add(newRec);
            db.SaveChanges();

            string newFileName = func.getUserVideoFileName(userID, newRec.VideoID, FileUpload.FileName);
            FileUpload.SaveAs(Server.MapPath("/videos/") + newFileName);
            newRec.Path = newFileName;
           

            db.SaveChanges();

            Response.Redirect("VideoUpdate.aspx?no=" + newRec.VideoID);

            submitButton.Enabled = false;

            showMessage("Your video has been successfully saved...");

            
        }
        catch (Exception ex)
        {
            showMessage("Error:" + ex.ToString());
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
}