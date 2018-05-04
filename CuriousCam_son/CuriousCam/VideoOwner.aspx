<%@ Page Language="C#" AutoEventWireup="true" CodeFile="VideoOwner.aspx.cs" Inherits="VideoOwner" MasterPageFile="~/MasterPage.master"%>

<asp:Content ID="head" ContentPlaceHolderID="head_holder" Runat="Server">
</asp:Content>

<asp:Content ID="content" ContentPlaceHolderID="content_holder" Runat="Server">
    <div class="row">
		<!-- Page Header -->
		<div class="col-lg-12">
			<h1 class="page-header text-primary"><i class="fa fa-chevron-circle-right fa-fw"></i>Instructor Profile</h1>
		</div>
		<!--End Page Header -->
	</div>
    <h3 class="text-center" id="ownerName" runat="server"></h3> 
    <div class="row border-primary">
	<div class="col-md-4">
		<p class="text-center">
            <asp:Image id="userImage" runat="server" CssClass="margin-5" ImageUrl="photos/person.jpg" width="261" height="300" style="border:2px solid;"></asp:Image>
		</p>
        <br />
		<table class="table">
        <tr>
			<th>Email:</th>
			<td><asp:Label ID="emailLabel" runat="server"></asp:Label></td>
		</tr>
		<tr>
			<th>Faculty:</th>
			<td><asp:Label ID="facultyLabel" runat="server"></asp:Label></td>
		</tr>
        <tr>
			<th>Department:</th>
			<td><asp:Label ID="departmentLabel" runat="server"></asp:Label></td>
		</tr>

		</table>
	</div>

	<div class="col-md-8">
		<h4 class="text-red"><i class="fa fa-video-camera"></i> Video List</h4>
        <div id="videoList" runat="server" class="margin-10">
            No video yet.
        </div>
	</div>
</div>

</form>
</asp:Content>

<asp:Content ID="footer" ContentPlaceHolderID="footer_holder" Runat="Server">
</asp:Content>