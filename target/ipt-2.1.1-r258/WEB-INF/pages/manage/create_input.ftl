<#include "/WEB-INF/pages/inc/header.ftl">
	<title><@s.text name="manage.resource.create.title"/></title>
 <#assign currentMenu = "manage"/>
<#include "/WEB-INF/pages/inc/menu.ftl">

<#include "/WEB-INF/pages/macros/forms.ftl"/>
<h1><@s.text name="manage.resource.create.title"/></h1>
<#include "inc/create_new_resource.ftl"/>

<#include "/WEB-INF/pages/inc/footer.ftl">
<script type="text/javascript" src="${baseURL}/js/jquery/jquery.min-1.5.1.js"></script>       
<script type="text/javascript" src="${baseURL}/js/jquery/jquery-ui.min-1.8.3.js"></script>
<script type="text/javascript" src="${baseURL}/js/jconfirmation.jquery.js"></script>
<script type="text/javascript">
    $(document).ready(function(){
        initHelp();
        $("#resourceType").val('occurrence').unwrap().hide();
        $(".infoImg").val('occurrence').hide();
        $('label[for="resourceType"]').hide();
        $('.confirmCreation').jConfirmAction({question : "<@s.text name='create.confirm'/>", yesAnswer : "<@s.text name='create.yes'/>", cancelAnswer : "<@s.text name='create.no'/>"});
    });
</script>
