using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.IO;

public class Functions : System.Web.UI.Page
{
    public string errorMessage;
    public string stateMessage;

	public Functions() 
	{

	}

    //****************************************************************************
    //******************  States or numbers to Strings ***************************
    //****************************************************************************

    public bool isImage(string fileName)
    {
        string ext = Path.GetExtension(fileName);

        return ext == ".gif" || ext == ".jpg" || ext == ".png" || ext==".jpeg";
    }

    public bool isVideo(string fileName)
    {
        string ext = Path.GetExtension(fileName);

        return ext == ".mp4" || ext == ".ogv" || ext == ".webm" || ext == ".3gp";
    }

    public string getUserTypeName(int state)
    {
        switch (state)
        {
            case 0:
                return "Staff";
            case 1:
                return "Instructor";
            case 2:
                return "Student";
            default:
                return "Unknown user type";
        }
    }


    //****************************************************************************
    //************************** Database Functions ********************************
    //****************************************************************************

    public string getUserVideoFileName(int userID, int videoID, string fileName)
    {
        return  "video" + userID.ToString() + "_" + videoID.ToString() + Path.GetExtension(fileName);
    }

    public string setUserPhoto(string email, string fileName)
    {
        if (fileName == null)
            return "person.jpg";

        CuriousCamEntities db = new CuriousCamEntities();

        Users u = (from x in db.Users
                       where x.Email == email
                       select x).SingleOrDefault();

        if (u != null)
        {
            String path = Server.MapPath("/Photos/");
            string newFileName = "user" + u.UserID + Path.GetExtension(fileName);
           
            u.Photo = newFileName;
            db.SaveChanges();

            return newFileName;
            
        }
        else
        {
            errorMessage = "Wrong user email:" + email;
        }

       return "person.jpg";
    }
   public string getUserName(int userID)
    {
       
        CuriousCamEntities db = new CuriousCamEntities();

        String name = (from x in db.Users
                        where x.UserID == userID
                        select x.Name + " " + x.Surname).SingleOrDefault();

        if (name != null)
        {
            return name;
        }
        else
        {
            return "Wrong User ID";
        }
    }

   public int getUserId(string email)
   {

       CuriousCamEntities db = new CuriousCamEntities();

       int id = (from x in db.Users
                      where x.Email== email
                      select x.UserID).SingleOrDefault();

       if (id != null)
       {
           return id;
       }
       else
       {
           return -1;
       }
   }

   public string getTopicName(int topicID)
   {

       CuriousCamEntities db = new CuriousCamEntities();

       String name = (from x in db.Topics
                      where x.TopicID == topicID
                      select x.Title).SingleOrDefault();

       if (name != null)
       {
           return name;
       }
       else
       {
           return "Wrong Topic ID";
       }
   }

   public string getSubTopicName(int subTopicID)
   {

       CuriousCamEntities db = new CuriousCamEntities();

       String name = (from x in db.SubTopics
                      where x.SubTopicID == subTopicID
                      select x.SubTopic).SingleOrDefault();

       if (name != null)
       {
           return name;
       }
       else
       {
           return "Wrong Sub Topic ID";
       }
   }

   public string getFacultyName(int facultyID)
   {

       CuriousCamEntities db = new CuriousCamEntities();

       String name = (from x in db.Faculties
                      where x.FacultyID == facultyID
                      select x.Name).SingleOrDefault();

       if (name != null)
       {
           return name;
       }
       else
       {
           return "Wrong Faculty ID";
       }
   }

   public string getDepartmentName(int departmanID)
   {

       CuriousCamEntities db = new CuriousCamEntities();

       String name = (from x in db.Departments
                      where x.DepartmentID == departmanID
                      select x.Name).SingleOrDefault();

       if (name != null)
       {
           return name;
       }
       else
       {
           return "Wrong Department ID";
       }
   }

   public bool removeUser(int userID)
   {
       //Database
       CuriousCamEntities db = new CuriousCamEntities();

       //Table
       Users c = (from x in db.Users
                  where x.UserID == userID
                  select x).SingleOrDefault();
       if (c != null)
       {
           string fileFullPath = Server.MapPath("/images/") + c.Photo;

           if (c.UserType==1 && !deleteUserVideos(userID))
           {
               errorMessage = "The user could no be deleted. Because his or her vides cannot be deleted: Details:" + errorMessage;
               return false;
           }


           db.Users.Remove(c);
           db.SaveChanges();

           if (File.Exists(fileFullPath))
           {
               File.Delete(fileFullPath);
           }

           stateMessage = "User has been deleted successfully";

           return true;

       }
       else
       {
           errorMessage= "Wrong Video ID:" + userID.ToString();
       }

       return false;
   }


   public bool deleteUserVideos(int userID)
   {
       CuriousCamEntities db = new CuriousCamEntities();

       // Delete the rest
       //Table
       Videos[] p = (from x in db.Videos
                     where x.UserID == userID
                     select x).ToArray();
       //New values
       if (p != null)
       {
           if (p.Length > 0)
           {
               for (int i = 0; i < p.Length; i++)
               {
                   string fileFullPath = Server.MapPath("/videos/") + p[i].Path;
                   try
                   {
                       db.Videos.Remove(p[i]);

                       if (File.Exists(fileFullPath))
                       {
                           File.Delete(fileFullPath);
                       }
                   }
                   catch (Exception ex)
                   {
                       errorMessage = ex.ToString();
                       return false;
                   }
                   
               }

               db.SaveChanges();

               return true;
           }
       }

       return false;

   }

   
    //****************************************************************************
   //****************** Session Management and Authorization ********************
   //****************************************************************************

   public void setSessionVar(int usertype, bool admin, string email, string name, string surname, int userid, string photo, Boolean state)
   {
       Session["usertype"] = usertype; // Guest
       Session["admin"] = admin;
       Session["email"] = email.Trim();
       Session["name"] = name.Trim();
       Session["surname"] = surname.Trim();
       Session["userid"] = userid;
       Session["photo"] = photo.Trim();
       Session["login"] = state;
   }

   //Is the user approved and active
   public Boolean loggedIn(){

        if (Session["login"] != null)
        {
            return Convert.ToBoolean(Session["login"]);
        }
        else
        {
            return false;
        }
        
    }
   
   public Boolean isAdmin()
   {
    return Convert.ToInt32(Session["admin"]) == 1 ? true: false;
   }

   public Boolean isInstractor()
   {
       if (getUserType() == 1)
           return true;
       else
           return false;
   }

   public Boolean isStudent()
   {
       if (getUserType() == 2)
           return true;
       else
           return false;
   }

   public int getUserType()
    {
        if (Session["usertype"] != null)
            return Convert.ToInt32(Session["usertype"]);
        else
            return -1;
    }


   public string getUserTypeName()
   {
       return getUserTypeName(getUserType());
   }

   public int getUserId()
    {
        if (Session["userid"] != null)
            return Convert.ToInt32(Session["userid"]);
        else
            return -1;
    }

}