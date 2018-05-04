<%@ Page Language="C#" AutoEventWireup="true" CodeFile="VideoShow.aspx.cs" Inherits="VideoShow" MasterPageFile="~/MasterPage.master"%>

<asp:Content ID="head" ContentPlaceHolderID="head_holder" Runat="Server">
</asp:Content>

<asp:Content ID="content" ContentPlaceHolderID="content_holder" Runat="Server">
    <div class="row">
		<!-- Page Header -->
		<div class="col-lg-12">
			<h1 class="page-header text-primary"><i class="fa fa-chevron-circle-right fa-fw"></i>Video Show</h1>
		</div>
		<!--End Page Header -->
	</div>
    <h3 id="videoTitle" runat="server"></h3> 
    <div class="row">
        <div class="col-md-4 text-center">
           <video id="video" runat="server" width='320' height='240' controls>" + 
                <source id="videoSrc1" runat="server" type='video/mp4'>
                <source id="videoSrc2" runat="server" type='video/webm'>
                <source id="videoSrc3" runat="server" type='video/ogg'>
                <source id="videoSrc4" runat="server" type='video/3gp'>
                Your browser does not support the video tag.
             </video>
        </div>
        <div class="col-md-8">
            <br />
            <br />
            <table class="table">
            <tr>
                <th style="width:150px;">Owner:</th>
                <td><asp:HyperLink id="ownerLink" runat="server">
                        <asp:Label ID="ownerLabel" runat="server"></asp:Label>
                    </asp:HyperLink></td>
            </tr>
            <tr>
                <th>Topic:</th>
                <td><asp:Label ID="topicLabel" runat="server"></asp:Label></td>
            </tr>
            <tr>
                <th>Sub Topic:</th>
                <td><asp:Label ID="subTopicLabel" runat="server"></asp:Label></td>
            </tr>
            <tr>
                <th>Upload Date:</th>
                <td><asp:Label ID="dateLabel" runat="server"></asp:Label></td>
            </tr>
        </table>
        </div>
    </div>
</asp:Content>

<asp:Content ID="footer" ContentPlaceHolderID="footer_holder" Runat="Server">
</asp:Content>