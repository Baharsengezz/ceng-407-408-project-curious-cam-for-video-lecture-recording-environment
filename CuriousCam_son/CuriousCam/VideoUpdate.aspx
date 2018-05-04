<%@ Page Language="C#" AutoEventWireup="true" CodeFile="VideoUpdate.aspx.cs" Inherits="VideoUpdate" MasterPageFile="~/MasterPage.master"%>

<asp:Content ID="head" ContentPlaceHolderID="head_holder" Runat="Server">
</asp:Content>

<asp:Content ID="content" ContentPlaceHolderID="content_holder" Runat="Server">
    <div class="row">
		<!-- Page Header -->
		<div class="col-lg-12">
			<h1 class="page-header text-primary"><i class="fa fa-chevron-circle-right"></i>Video Update & Delete</h1>
		</div>
		<!--End Page Header -->
	</div>

    <form  role="form" runat="server" enctype="multipart/form-data">
    <div class="row border-primary">
	    <div class="col-md-4 text-center">
		    <video id="video" runat="server" width='320' height='240' controls>" + 
                    <source id="videoSrc1" runat="server" type='video/mp4'>
                    <source id="videoSrc2" runat="server" type='video/webm'>
                    <source id="videoSrc3" runat="server" type='video/ogg'>

                    Your browser does not support the video tag.
             </video>
		    <table class="margin-5">
		    <tr>
			    <td><strong>Change Video:&nbsp;</strong></td>
			    <td><asp:FileUpload  ID="FileUpload" runat="server"/></td>
		    </tr>
		    </table>
	    </div>

	    <div class="col-md-8" style="border-right:1px dashed gray;">
                    <div class="form-group">
                                <asp:Label ForeColor="Red" ID="messageLabel" runat="server"  style="font-size: small" Visible="False"></asp:Label>
                    </div>
                     <div class="form-group">
				        <label>Title:</label>
				        <asp:TextBox runat="server" Cssclass="form-control" id="titleText" Required="true" ></asp:TextBox>
			        </div>
                    <div class="form-group">
				        <label class="text-success">Topic:</label>
                        <asp:DropDownList AutoPostBack="True" Cssclass="form-control" ID="topicDDList" runat="server"  DataSourceID="registerSqlDS" DataTextField="Title" DataValueField="TopicID" Required="true" OnTextChanged="topicDDList_TextChanged" >
                        </asp:DropDownList>
				        <asp:SqlDataSource ID="registerSqlDS" runat="server" ConnectionString="<%$ ConnectionStrings:CuriousCamConnectionString %>" SelectCommand="SELECT * FROM [Topics]"></asp:SqlDataSource>
			        </div>
                    <div class="form-group">
				        <label class="text-success">Sub Topic:</label>
                        <asp:DropDownList Cssclass="form-control" ID="subTopicDDList" runat="server" Required="true">
                        </asp:DropDownList>
			        </div>
                    <div class="form-group">
				        <label>Upload Date:</label>
                        <asp:Label ID="uploadDateLabel" runat="server"></asp:Label>
			        </div>
			        <div class="form-group text-right">
				        <asp:Button runat="server" ID="submitButton" Cssclass="btn btn-primary" Text="Update" OnClick="updateButton_Click"></asp:Button>
                        <asp:Button runat="server" ID="deleteButton" OnClientClick="return confirm('Are you sure to delete?');" Cssclass="btn btn-danger" Text="Delete" OnClick="deleteButton_Click"></asp:Button>
			        </div>
             
        </div>
    </div> 
</form> 
</asp:Content>

<asp:Content ID="footer" ContentPlaceHolderID="footer_holder" Runat="Server">
</asp:Content>