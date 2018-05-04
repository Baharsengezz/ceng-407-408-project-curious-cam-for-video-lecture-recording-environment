using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class _Default : System.Web.UI.Page
{
     //Toolbox
    Functions func = new Functions();

    protected void Page_Load(object sender, EventArgs e)
    {
        pageTitleLabel.Text = func.getUserTypeName() + " Home Page";
    }

}