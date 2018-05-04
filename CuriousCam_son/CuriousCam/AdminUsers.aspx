<%@ Page Language="C#" AutoEventWireup="true" CodeFile="AdminUsers.aspx.cs" Inherits="AdminUsers" MasterPageFile="~/MasterPage.master"%>

<asp:Content ID="head" ContentPlaceHolderID="head_holder" Runat="Server">
</asp:Content>

<asp:Content ID="content" ContentPlaceHolderID="content_holder" Runat="Server">
    <div class="row">
		<!-- Page Header -->
		<div class="col-lg-12">
			<h1 class="page-header text-primary"><i class="fa fa-chevron-circle-right fa-fw"></i><asp:Label ID="pageTitle" runat="server"></asp:Label></h1>
		</div>
		<!--End Page Header -->
	</div>

    
    <div id="userList" runat="server">...</div>

      
     
</asp:Content>

<asp:Content ID="footer" ContentPlaceHolderID="footer_holder" Runat="Server">
</asp:Content>