<%@ Page Language="C#" AutoEventWireup="true" CodeFile="MyProfile.aspx.cs" Inherits="MyProfile" MasterPageFile="~/MasterPage.master"%>

<asp:Content ID="head" ContentPlaceHolderID="head_holder" Runat="Server">
</asp:Content>

<asp:Content ID="content" ContentPlaceHolderID="content_holder" Runat="Server">
    <div class="row">
		<!-- Page Header -->
		<div class="col-lg-12">
			<h1 class="page-header text-primary"><i class="fa fa-chevron-circle-right fa-fw"></i>My Profile</h1>
		</div>
		<!--End Page Header -->
	</div>
  
  <form  role="form" runat="server" enctype="multipart/form-data">

    <div class="row border-primary">
	<div class="col-md-4 text-center">
		<asp:Image id="userImage" runat="server" CssClass="margin-5" ImageUrl="photos/person.jpg" width="261" height="300" style="border:2px solid;"></asp:Image>
		<table class="margin-5">
		<tr>
			<td><strong>Change Image:&nbsp;</strong></td>
			<td><asp:FileUpload  ID="FileUpload" runat="server"/></td>
		</tr>
		</table>
	</div>

	<div class="col-md-8" style="border-right:1px dashed gray;">
            <div class="form-group">
                        <asp:Label ForeColor="Red" ID="messageLabel" runat="server" Text="Please enter your user name and password." style="font-size: small" Visible="False"></asp:Label>
            </div>
			<div class="form-group">
				<label class="text-success">User Type:</label>
                <asp:DropDownList Cssclass="form-control" ID="userTypeDDList" runat="server" Required="true">
                    <asp:ListItem Value="1">Instractor</asp:ListItem>
                    <asp:ListItem Value="2">Student</asp:ListItem>
                    <asp:ListItem Value="0">Other Staff</asp:ListItem>
                </asp:DropDownList>
			</div>
            <div class="form-group">
				<label class="text-success">Faculty:</label>
                <asp:DropDownList Cssclass="form-control" ID="facultyDDList" runat="server" AutoPostBack="True" DataSourceID="registerSqlDS" DataTextField="Name" DataValueField="FacultyID" OnTextChanged="facultyDDlist_OnChange" Required="true" >
                </asp:DropDownList>
				<asp:SqlDataSource ID="registerSqlDS" runat="server" ConnectionString="<%$ ConnectionStrings:CuriousCamConnectionString %>" SelectCommand="SELECT [FacultyID], [Name] FROM [Faculties]"></asp:SqlDataSource>
			</div>
            <div class="form-group">
				<label class="text-success">Department:</label>
                <asp:DropDownList Cssclass="form-control" ID="departmentDDList" runat="server" Required="true">
                </asp:DropDownList>
			</div>
			<div class="form-group">
				<label>Name:</label>
				<asp:TextBox runat="server" Cssclass="form-control" id="nameText" Required="true" ></asp:TextBox>
			</div>
			<div class="form-group">
				<label>Surname:</label>
				<asp:TextBox runat="server" Cssclass="form-control" id="surnameText" Required="true" ></asp:TextBox>
			</div>
            <div class="form-group">
				<label>E-mail:</label>
				<asp:TextBox runat="server" Cssclass="form-control" id ="emailText" Required="true"></asp:TextBox>
				<p class="help-block">info@xyz.com</p>
			</div>
			<div class="form-group">
				<label>Phone:</label>
				<asp:TextBox runat="server" Cssclass="form-control" id="phoneText" Required="true"></asp:TextBox>
				<p class="help-block">(555) 555 55-55</p>
			</div>
			<div class="form-group text-right">
					<asp:Button runat="server" ID="updateButton" Cssclass="btn btn-primary" Text="Update" OnClick="updateButton_Click"></asp:Button>
			</div>
	</div>
</div>

</form>
</asp:Content>

<asp:Content ID="footer" ContentPlaceHolderID="footer_holder" Runat="Server">
</asp:Content>