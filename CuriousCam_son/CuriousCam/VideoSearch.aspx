<%@ Page Language="C#" AutoEventWireup="true" CodeFile="VideoSearch.aspx.cs" Inherits="VideoSearch" MasterPageFile="~/MasterPage.master"%>

<asp:Content ID="head" ContentPlaceHolderID="head_holder" Runat="Server">
</asp:Content>

<asp:Content ID="content" ContentPlaceHolderID="content_holder" Runat="Server">
    <div class="row">
		<!-- Page Header -->
		<div class="col-lg-12">
			<h1 class="page-header text-primary"><i class="fa fa-chevron-circle-right"></i>Videos</h1>
		</div>
		<!--End Page Header -->
	</div>


    <form class="container-fluid margin-10" method="post">
		<!-- search section-->
		<div class="input-group custom-search-form">
			<input type="text" name="searchText" class="form-control" placeholder="Video Search..." value="<% Response.Write(Request.Form["searchText"]); %>">
			<span class="input-group-btn">
				<button class="btn btn-default" type="submit">
					<i class="fa fa-search"></i>
				</button>
			</span>
		</div>
	</form>

    

    <div>
        <% 
            string title = Request.Form["searchText"];
            string cat = Request.QueryString["cat"];
            int userID = Convert.ToInt32(Session["userid"]);

            CuriousCamEntities db = new CuriousCamEntities();

            Videos[] v;

            if (title != null)
            {
                if (cat == "my")
                    v = (from x in db.Videos
                         where x.Title.Contains(title) && x.UserID==userID
                         select x).ToArray();
                else
                    v = (from x in db.Videos
                         where x.Title.Contains(title)
                         select x).ToArray();
                    
            }
            else
            {
                if (cat == "my")
                        v = (from x in db.Videos
                             where x.UserID == userID
                             select x).ToArray();
                        
                else
                         v = (from x in db.Videos
                             select x).ToArray();
            }
       
            
            if (v != null)
            {
                Response.Write("<ul class='list-group'>");
                string html = "";
                for (int i = 0; i < v.Length; i++)
                {

                    html = "<li class='list-group-item'>";
                    html +="<h5><a href='VideoShow.aspx?no=" + v[i].VideoID.ToString() + "'>" +
                           "<b>" + v[i].Title + "</b></a> <small><i>(" + v[i].UploadDate.ToShortDateString() + ")</i></small></h5>";

                    if (Session["admin"].ToString() == "True" || v[i].UserID.ToString()==Session["userid"].ToString())
                        html += "<p class='text-right'><a href='VideoUpdate.aspx?no=" + v[i].VideoID + "'class='btn btn-primary'>Update&Delete<a></p>";
                           
                    html += "</li>";

                    Response.Write(html);
                }

                Response.Write("</ul>");
            }
            else
            {
                Response.Write("There is no video to list yet...");
            }
       
       
        %> 
    </div>
</asp:Content>

<asp:Content ID="footer" ContentPlaceHolderID="footer_holder" Runat="Server">
</asp:Content>