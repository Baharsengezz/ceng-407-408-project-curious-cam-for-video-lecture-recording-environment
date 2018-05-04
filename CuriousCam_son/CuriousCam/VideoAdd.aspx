<%@ Page Language="C#" AutoEventWireup="true" CodeFile="VideoAdd.aspx.cs" Inherits="VideoAdd" MasterPageFile="~/MasterPage.master"%>

<asp:Content ID="head" ContentPlaceHolderID="head_holder" Runat="Server">
</asp:Content>

<asp:Content ID="content" ContentPlaceHolderID="content_holder" Runat="Server">
    <div class="row">
		<!-- Page Header -->
		<div class="col-lg-12">
			<h1 class="page-header text-primary"><i class="fa fa-chevron-circle-right"></i>Add New Video</h1>
		</div>
		<!--End Page Header -->
	</div>
    <div>
         <form  role="form" runat="server" enctype="multipart/form-data">
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
				<label>Video File:</label>
			    <asp:FileUpload Cssclass="form-control" ID="FileUpload" runat="server" Required="true"/>
			</div>
			<div class="form-group text-right">
				<Button ID="resetButton" class="btn btn-success" Type="Reset">Reset</Button>
				<asp:Button runat="server" ID="submitButton" Cssclass="btn btn-primary" Text="Submit" OnClick="btnRegister_Click"></asp:Button>
			</div>
		</form>
    </div>  
</asp:Content>

<asp:Content ID="footer" ContentPlaceHolderID="footer_holder" Runat="Server">
</asp:Content>