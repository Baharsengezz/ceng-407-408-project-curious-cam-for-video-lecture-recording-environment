<%@ Page Title="" Language="C#" MasterPageFile="~/MasterPage.master" AutoEventWireup="true" CodeFile="Logout.aspx.cs" Inherits="Default2" %>

<asp:Content ID="Content1" ContentPlaceHolderID="head_holder" Runat="Server">
    <style type="text/css">
        .auto-style9 {
            font-size: medium;
        }
    </style>
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="content_holder" Runat="Server">
    <div class="row">
		<!-- Page Header -->
		<div class="col-lg-12">
			<h1 class="page-header text-primary"><i class="fa fa-chevron-circle-right"></i> Home Page</h1>
		</div>
		<!--End Page Header -->
    </div>

    <div>
        <asp:Label ID="nameLabel" runat="server" ForeColor="Red" style="font-weight: 700; font-size: large"></asp:Label>
        <br />
        <br />
        <strong><span class="auto-style9">You have been successfully logged out...</span></strong>
    </div>
    <br />
    <br />
</asp:Content>
<asp:Content ID="Content3" ContentPlaceHolderID="footer_holder" Runat="Server">
</asp:Content>

