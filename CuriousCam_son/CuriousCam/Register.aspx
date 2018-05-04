<%@ Page Title="" Language="C#" AutoEventWireup="true" CodeFile="Register.aspx.cs" Inherits="Register" %>


<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="content-language" content="tr-TR" />
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<meta name="author" content="Elifnur Altuntaş" />
	<meta name="description" content="Curious Cam Video Sahring Web Site" />
	<meta name="keywords" content="Curious Cam, Lecture, Course, Video, Sharing" />

	<meta name="robots" content="all" />
	<meta name="robots" content="index, follow" />
	<meta name="copyright" content="Copyright - Tüh Hakları Saklıdır. 2018 www.curiouscam.com" />	


    <title>CriousCam</title>

    <!-- Core CSS - Include with every page -->
    <link href="assets/plugins/bootstrap/bootstrap.css" rel="stylesheet" />
	<link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.6.3/css/font-awesome.min.css">

    <link href="assets/plugins/pace/pace-theme-big-counter.css" rel="stylesheet" />
    <link href="assets/css/style.css" rel="stylesheet" />
    <link href="assets/css/main-style.css" rel="stylesheet" />

	<link rel="shortcut icon" href="icon.png" type="image/x-png" sizes="32x32" />
	<link rel="icon" type="image/x-png" href="ico.png" sizes="32x32" />

	<script type="text/javascript" src="main-js.js"></script>
	<link href="main-css.css" rel="stylesheet" />
</head>


<!-- Ajax Kodu-->
<script type="text/javascript">
    function sifreKontrol(e) {
        var sifre1 = document.getElementById("password1Text").value;
        var sifre2 = document.getElementById("password2Text").value;

        if (sifre1 != sifre2) {
            e.preventDefault();
            alert("Şifreler aynı olmalıdır.");
            return false;
        }

        return true;
    }
</script>

<body class="body-Login-back">

<!--  Modals-->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 id="modal_baslik" class="modal-title" id="myModalLabel">Modal title</h4>
				</div>
				<div id="modal_mesaj" class="modal-body">
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Tamam</button>
				</div>
			</div>
		</div>
	</div>

	<button id="modal_btn" style="display:none;" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal"></button>
	 <!-- End Modals-->

    <div class="container-fluid">
       
        <div class="row">
            <div class="col-md-4 col-md-offset-4 text-center">
			<br/>
              <img src="assets/img/logouzun.png" alt=""/>
                </div>
            <div class="col-md-4 col-md-offset-4">
                <div class="login-panel panel panel-default">                  
                    <div class="panel-heading text-center">
                        <h3 class="panel-title">Registration Form</h3>
                    </div>
                    <div class="panel-body">
                        <form  role="form" runat="server" enctype="multipart/form-data" onsubmit="sifreKontrol(event);">
                            <div class="form-group">
                                     <asp:Label ForeColor="Red" ID="messageLabel" runat="server" Text="Please enter your user name and password." style="font-size: small" Visible="False"></asp:Label>
                            </div>
							<div class="form-group">
								<label class="text-success">User Type:</label>
                                <asp:DropDownList Cssclass="form-control" ID="userTypeDDList" runat="server" Required="true">
                                    <asp:ListItem Value="1">Instructor</asp:ListItem>
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
								<asp:TextBox runat="server" Cssclass="form-control" id ="emailText" Required="true" type="email"></asp:TextBox>
								<p class="help-block">info@xyz.com</p>
							</div>
							<div class="form-group">
								<label class="text-primary">Password:</label>
								<asp:TextBox runat="server" Cssclass="form-control" id="password1Text" TextMode="Password" Required="true"></asp:TextBox>
							</div>
							<div class="form-group">
								<label class="text-primary">Password (Again):</label>
								<asp:TextBox runat="server" Cssclass="form-control" id="password2Text" TextMode="Password" Required="true"></asp:TextBox>
							</div>
							<div class="form-group">
								<label>Phone:</label>
								<asp:TextBox runat="server" Cssclass="form-control" id="phoneText" Required="true"></asp:TextBox>
								<p class="help-block">(555) 555 55-55</p>
							</div>
							<div class="form-group">
								<label>Photo:</label>
                                <asp:FileUpload Cssclass="form-control" ID="FileUpload" runat="server" Required="true"/>
							</div>
                            <div class="form-group">
								 <asp:Image ID="Image" Cssclass="form-control" runat="server" ImageUrl="~/images/person.jpg"  Style="min-height:150px;" />
							</div>
							<div class="form-group text-right">
								<Button ID="resetButton" class="btn btn-success" Type="Reset">Reset</Button>
							    <asp:Button runat="server" ID="submitButton" Cssclass="btn btn-primary" Text="Submit" OnClick="btnRegister_Click"></asp:Button>
							</div>
						</form>
                        <p class="text-center"><a href="Login.aspx">Login...</a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>

     <!-- Core Scripts - Include with every page -->
    <script src="assets/plugins/jquery-1.10.2.js"></script>
    <script src="assets/plugins/bootstrap/bootstrap.min.js"></script>
    <script src="assets/plugins/metisMenu/jquery.metisMenu.js"></script>

</body>

</html>

