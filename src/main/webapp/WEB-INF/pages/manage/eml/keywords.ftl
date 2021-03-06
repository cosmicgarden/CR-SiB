<#escape x as x?html>
<#setting number_format="#####.##">
<#include "/WEB-INF/pages/inc/header.ftl">
<title><@s.text name='manage.metadata.keywords.title'/></title>
<#include "/WEB-INF/pages/macros/metadata.ftl"/>
 <#assign sideMenuEml=true />
 <#assign currentMenu="manage"/>
<script type="text/javascript">
$(document).ready(function(){
	initHelp();
	<#if eml.keywords??>
		<#assign numel=eml.keywords?size>
		<#if numel==1>
			var ocuf=$('#eml\\.keywords\\[0\\]\\.keywordsString').val();
			if(ocuf=="Occurrence"){
				$('#eml\\.keywords\\[0\\]\\.keywordsString').val('');
			}
		</#if>
		<#if (numel>0)>
			var nulc=${numel};
			for(var i=0;i<nulc;i++){
				var selector='#eml\\.keywords\\['+i+'\\]\\.keywordsString';
				var textr=$(selector).val();
				textr=textr.replace(/null/g,'');
				$(selector).val(textr);
			}
		</#if>
	</#if>
});
</script>
<#include "/WEB-INF/pages/inc/menu.ftl">
<#include "/WEB-INF/pages/macros/forms.ftl"/>


<h1><span class="superscript"><@s.text name='manage.overview.title.label'/></span>
    <a href="resource.do?r=${resource.shortname}" title="${resource.title!resource.shortname}">${resource.title!resource.shortname}</a>
</h1>
<div class="">

<form class="topForm" action="metadata-${section}.do" method="post">
	<h2 class="subTitle"><@s.text name='manage.metadata.keywords.title'/></h2>
    <p><@s.text name='manage.metadata.keywords.intro'/></p>
	<div id="items">
		<#list eml.keywords as item>
			<div id="item-${item_index}" class="item">
			<div class="newline"></div>
			<div class="right">
				<a id="removeLink-${item_index}" class="removeLink" href="">[ <@s.text name='manage.metadata.removethis'/> <@s.text name='manage.metadata.keywords.item'/> ]</a>
		    </div>
		    <div style="display:none;">
			<@input name="eml.keywords[${item_index}].keywordThesaurus" i18nkey="eml.keywords.keywordThesaurus" help="i18n" requiredField=true />
			</div>
			<@text name="eml.keywords[${item_index}].keywordsString" i18nkey="eml.keywords.keywordsString" help="i18n" requiredField=true/>
		  	</div>
		</#list>
	</div>
	<div class="addNew"><a id="plus" href=""><@s.text name='manage.metadata.addnew'/> <@s.text name='manage.metadata.keywords.item'/></a></div>
	<div class="buttons">
		<@s.submit cssClass="button" name="save" key="button.save" />
		<@s.submit cssClass="button" name="cancel" key="button.cancel" />
	</div>
	<!-- internal parameter -->
	<input name="r" type="hidden" value="${resource.shortname}" />
</form>
</div>
<div id="baseItem" class="item" style="display:none;">
	<div class="right">
      <a id="removeLink" class="removeLink" href="">[ <@s.text name='manage.metadata.removethis'/> <@s.text name='manage.metadata.keywords.item'/> ]</a>
    </div>
    <div style="display:none;">
	<@input name="keywordThesaurus" i18nkey="eml.keywords.keywordThesaurus" help="i18n" requiredField=true value="N/A"/>
	</div>
	<@text name="keywordsString" i18nkey="eml.keywords.keywordsString" help="i18n" requiredField=true/>
</div>
<#include "/WEB-INF/pages/inc/footer.ftl">
</#escape>
