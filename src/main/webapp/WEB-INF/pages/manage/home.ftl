<#escape x as x?html>
  <#include "/WEB-INF/pages/inc/header.ftl">
<title><@s.text name="title"/></title>
  <#assign currentMenu = "manage"/>
  <#include "/WEB-INF/pages/inc/menu.ftl">
  <#include "/WEB-INF/pages/macros/forms.ftl"/>
  <#include "/WEB-INF/pages/macros/resourcesTableManage.ftl"/>
<script type="text/javascript" src="${baseURL}/js/jquery/jquery.min-1.5.1.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery/jquery-ui.min-1.8.3.js"></script>
<script type="text/javascript" language="javascript" src="${baseURL}/js/jquery/jquery.dataTables.js"></script>
<script type="text/javascript" language="javascript" src="${baseURL}/js/jquery/jquery.dataTables.columnFilter.js"></script>
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
<div id="new-resource">

  <h2><img src="${baseURL}/images/icons/new.png"><@s.text name="manage.resource.create.title"/></h2>
  <#include "inc/create_new_resource.ftl"/>

</div>
  <@resourcesTable shownPublicly=false numResourcesShown=10 sEmptyTable="manage.home.resources.none" columnToSortOn=6 sortOrder="desc"/>

<h1 class="rtableTitle"><img src="${baseURL}/images/icons/recursos.png"><@s.text name="manage.home.title"/></h1>
<div id="rtableContainer"></div>
  <#include "/WEB-INF/pages/inc/footer.ftl">
</#escape>