<%@ Page Title="" Language="C#" AutoEventWireup="true" CodeFile="Login.aspx.cs" Inherits="Login" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="content-language" content="tr-TR" />
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<meta name="author" content="Elifnur Altundaş" />
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

<body class="body-Login-back">

    <div class="container">
       
        <div class="row">
            <div class="col-md-4 col-md-offset-4 text-center logo-margin ">
              <img src="assets/img/logo.png" alt=""/>
                </div>
            <div class="col-md-4 col-md-offset-4">
                <div class="login-panel panel panel-default">                  
                    <div class="panel-heading">
                        <h3 class="panel-title">Enter your email and password to login..</h3>
                    </div>
                    <div class="panel-body">
                        <form role="form" runat="server" >
                            <fieldset>
                                <div class="form-group">
                                     <label>E-mail:</label>
                                    <asp:TextBox Cssclass="form-control" runat="server" id="emailText" required></asp:TextBox>
                                </div>
                                <div class="form-group">
                                    <label>Password:</label>
                                    <asp:TextBox Cssclass="form-control" runat="server" id="passwordText" TextMode="Password" required></asp:TextBox>
                                </div>
                                <div class="form-group">
                                     <asp:Label ForeColor="Red" ID="messageLabel" runat="server" Text="Please enter your user name and password." style="font-size: small" Visible="False"></asp:Label>
                                </div>
                                <div class="form-group">
                                       <asp:CheckBox  runat="server" id="rememberCheckBox" Text=" Remember me"></asp:CheckBox>
                                </div>

                                <!-- Change this to a button or input when using this as a form -->
        						<div class="form-group">
									<asp:Button id="loginButton" runat="server" OnClick="loginButton_Click" class="btn btn-lg btn-success btn-block" Text="Log In" ></asp:Button>
								</div>							
								<div class="form-group">
                                    <label>
                                        <a href="Register.aspx" >Register...</a>
                                    </label>
                                </div>
                            </fieldset>
                        </form>
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

