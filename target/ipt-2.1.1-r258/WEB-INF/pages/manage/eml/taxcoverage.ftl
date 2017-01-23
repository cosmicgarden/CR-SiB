<#escape x as x?html>
<#setting number_format="#####.##">
<#include "/WEB-INF/pages/inc/header.ftl">
<#include "/WEB-INF/pages/macros/metadata.ftl"/>
<script type="text/javascript">
	$(document).ready(function(){
		initHelp();
		<#if eml.taxonomicCoverages??>
			<#assign num=eml.taxonomicCoverages?size>
			$( "#trash-0-0").hide();
			$( "#removeLink-0").hide();
		</#if>
			var txrc=$("label[for='eml.taxonomicCoverages[0].taxonKeywords[0].commonName']").text();
			//$("label[for='eml.taxonomicCoverages[0].taxonKeywords[0].commonName']").text(txrc+'*');
			var txrr=$("label[for='eml.taxonomicCoverages[0].taxonKeywords[0].rank']").text();
			//$("label[for='eml.taxonomicCoverages[0].taxonKeywords[0].rank']").text(txrr+'*');
			var descl=$("label[for='eml.taxonomicCoverages[0].description']").text();
			//$("label[for='eml.taxonomicCoverages[0].description']").text(descl+'*');

		/*
		$('#save').click(function(event){	
			//event.preventDefault();
			var allFT=true;
			var numel=$("[id^=item-]").length;
			var numels=$("#item-1 > #subItems > [id^=subItem-]").length;
			var txtfield=jQuery('#eml\\.taxonomicCoverages\\[1\\]\\.taxonKeywords\\[0\\]\\.scientificName').val();
			var descrip='';
			var txef='';
			var scfnf='';
			var scfet='';
			var rankt='';
			var ranke='';
			var commt='';
			var comme='';
			for(i=0;i<numel;i++){
				var numels=$("#item-"+i+" > #subItems > [id^=subItem-]").length;
				descrip=$("#eml\\.taxonomicCoverages\\["+i+"\\]\\.description").val();
				descrip=$.trim(descrip);
				if((descrip=='')||(descrip.length < 5)){
					allFT=false;
					if(!$('#desc-'+i).length){
						txef=$("label[for='eml.taxonomicCoverages\\["+i+"\\]\\.description']").text()+" es requerido";
						$('<ul class="fielderror" id="desc-'+i+'" ><li><span>'+txef+'</span></li></ul>').insertBefore('#eml\\.taxonomicCoverages\\['+i+'\\]\\.description');
					}
				}else{
					$('#desc-'+i).remove();
				}
				for(j=0;j<numels;j++){
					scfnf=$("#eml\\.taxonomicCoverages\\["+i+"\\]\\.taxonKeywords\\["+j+"\\]\\.scientificName").val();
					rankt=$("#eml\\.taxonomicCoverages\\["+i+"\\]\\.taxonKeywords\\["+j+"\\]\\.rank").val();
					commt=$("#eml\\.taxonomicCoverages\\["+i+"\\]\\.taxonKeywords\\["+j+"\\]\\.commonName").val();
					scfnf=$.trim(scfnf);
					rankt=$.trim(rankt);
					if((scfnf=='')||(scfnf.length < 3)){
						allFT=false;
						if(!$('#scfnt-'+i+'-'+j).length){
							scfet=$("label[for='eml.taxonomicCoverages\\["+i+"\\]\\.taxonKeywords\\["+j+"\\]\\.scientificName']").text()+"es requerido";
							$('<ul class="fielderror" id="scfnt-'+i+'-'+j+'" ><li><span>Nombre científico es requerido</span></li></ul>').insertBefore( '#eml\\.taxonomicCoverages\\['+i+'\\]\\.taxonKeywords\\['+j+'\\]\\.scientificName' );
						}
					}else{
						$('#scfnt-'+i+'-'+j).remove();
					}
					if(rankt==''){
						allFT=false;
						if(!$('#rankt-'+i+'-'+j).length){
							ranke=$("label[for='eml.taxonomicCoverages\\["+i+"\\]\\.taxonKeywords\\["+j+"\\]\\.rank']").text()+"es requerido";
							$('<ul class="fielderror" id="rankt-'+i+'-'+j+'" ><li><span>Categoría es requerido</span></li></ul>').insertBefore( '#eml\\.taxonomicCoverages\\['+i+'\\]\\.taxonKeywords\\['+j+'\\]\\.rank' );
						}
					}else{
						$('#rankt-'+i+'-'+j).remove();
					}
					if((commt=='')||(commt.length < 3)){
						allFT=false;
						if(!$('#commt-'+i+'-'+j).length){
							comme=$("label[for='eml.taxonomicCoverages\\["+i+"\\]\\.taxonKeywords\\["+j+"\\]\\.commonName']").text()+"es requerido";
							$('<ul class="fielderror" id="commt-'+i+'-'+j+'" ><li><span>Nombre común es requerido</span></li></ul>').insertBefore( '#eml\\.taxonomicCoverages\\['+i+'\\]\\.taxonKeywords\\['+j+'\\]\\.commonName' );
						}
					}else{
						$('#commt-'+i+'-'+j).remove();
					}
					scfnf='';
					rankt='';
					commt='';
				}
				descrip='';
			}

			if(!allFT){
				event.preventDefault();
            }
		});
		*/
	});
</script>
<title><@s.text name='manage.metadata.taxcoverage.title'/></title>
<#assign sideMenuEml=true />
<#assign currentMenu="manage"/>
<#include "/WEB-INF/pages/inc/menu.ftl">
<#include "/WEB-INF/pages/macros/forms.ftl"/>

<h1><span class="superscript"><@s.text name='manage.overview.title.label'/></span>
    <a href="resource.do?r=${resource.shortname}" title="${resource.title!resource.shortname}">${resource.title!resource.shortname}</a>
</h1>
<div class="">

<form class="topForm" action="metadata-${section}.do" method="post">
	<h2 class="subTitle"><@s.text name='manage.metadata.taxcoverage.title'/></h2>
    <p><@s.text name='manage.metadata.taxcoverage.intro'/></p>
	<div id="items">
		<!-- Adding the taxonomic coverages that already exists on the file -->
		<#assign next_agent_index=0 />
		<#list eml.taxonomicCoverages as item>
			<div id='item-${item_index}' class="item">
				<div class="right">
    				<a id="removeLink-${item_index}" class="removeLink" href="">[ <@s.text name='manage.metadata.removethis'/> <@s.text name='manage.metadata.taxcoverage.item'/> ]</a>
  				</div>
  				<@text  i18nkey="eml.taxonomicCoverages.description" help="i18n" name="eml.taxonomicCoverages[${item_index}].description" requiredField=true />
  				<!-- Taxon list-->
				<!-- a id="taxonsLink-${item_index}" class="show-taxonList" href="" ><@s.text name='manage.metadata.addseveral' /> <@s.text name='manage.metadata.taxcoverage.taxon.items' /></a> -->
				<@link name="taxonsLink-${item_index}" class="show-taxonList" value="manage.metadata.taxcoverage.addSeveralTaxa" help="i18n" i18nkey="manage.metadata.taxcoverage.addSeveralTaxa"/>
				<div id="list-${item_index}" class="half addSeveralTaxa" style="display:none">
					<@text i18nkey="eml.taxonomicCoverages.taxonList" help="i18n" name="taxon-list-${item_index}" value="" />
					<div id="addSeveralTaxaButtons" class="buttons">
						<@s.submit cssClass="button" name="add-button-${item_index}" key="button.add"/>
					</div>
				</div>
				<div id="subItems" class="clearfix">
					<#list item.taxonKeywords as subItem>
						<div id="subItem-${subItem_index}" class="sub-item grid_17 suffix_1 clearfix">
							<div class="third">
								<@input i18nkey="eml.taxonomicCoverages.taxonKeyword.scientificName" name="eml.taxonomicCoverages[${item_index}].taxonKeywords[${subItem_index}].scientificName" requiredField=true />
								<@input i18nkey="eml.taxonomicCoverages.taxonKeyword.commonName" name="eml.taxonomicCoverages[${item_index}].taxonKeywords[${subItem_index}].commonName"  />
								<@select i18nkey="eml.taxonomicCoverages.taxonKeyword.rank"  name="eml.taxonomicCoverages[${item_index}].taxonKeywords[${subItem_index}].rank" options=ranks value="${eml.taxonomicCoverages[item_index].taxonKeywords[subItem_index].rank!?lower_case}" />
								<#if (item.taxonKeywords ? size == 1) >
									<img id="trash-${item_index}-${subItem_index}" class="trash-icon" src="${baseURL}/images/trash-m.png" style="display: none;">
								<#else>
									<img id="trash-${item_index}-${subItem_index}" class="trash-icon" src="${baseURL}/images/trash-m.png">
								</#if>
							</div>
						</div>
					</#list>
				</div>
				<div style="margin-top:10px;font-size:0.92em;"><a id="plus-subItem-${item_index}" href="" ><@s.text name='manage.metadata.addnew' /> <@s.text name='manage.metadata.taxcoverage.taxon.item' /></a></div>
			</div>
		</#list>

		<#assign num=eml.taxonomicCoverages?size>
		
		<#if num==0>
			<div id="item-0" class="item clearfix" >
			<div class="right">
				<a id="removeLink-0" class="removeLink" href="">[ <@s.text name='manage.metadata.removethis'/> <@s.text name='manage.metadata.taxcoverage.item'/> ]</a>
			</div>

			<@text i18nkey="eml.taxonomicCoverages.description" help="i18n" name="eml.taxonomicCoverages[0].description" requiredField=true/>
			<!-- Taxon list-->
			<div class="addNew"><a id="taxonsLink-0" class="show-taxonList" href="" ><@s.text name='manage.metadata.taxcoverage.addSeveralTaxa' /></a></div>
			<div id="list-0" class="half clearfix" style="display:none">
				<@text i18nkey="eml.taxonomicCoverages.taxonList" help="i18n" name="taxon-list-0" value="" />
				<div class="buttons taxon-list">
					<@s.submit cssClass="button" name="add-button-0" key="button.add"/>
				</div>
			</div>
			<div id="subItems">
				<div id="subItem-0" class="sub-item grid_17 suffix_1 clearfix">
					<div class="third">
						<@input i18nkey="eml.taxonomicCoverages.taxonKeyword.scientificName" name="eml.taxonomicCoverages[0].taxonKeywords[0].scientificName"  />
						<@input i18nkey="eml.taxonomicCoverages.taxonKeyword.commonName" name="eml.taxonomicCoverages[0].taxonKeywords[0].commonName" />
						<@select i18nkey="eml.taxonomicCoverages.taxonKeyword.rank"  name="eml.taxonomicCoverages[0].taxonKeywords[0].rank" options=ranks />
						<img id="trash-0-0" class="trash-icon" src="${baseURL}/images/trash-m.png">
					</div>
				</div>
			</div>
			<div class="addNew"><a id="plus-subItem-0" href="" ><@s.text name='manage.metadata.addnew' /> <@s.text name='manage.metadata.taxcoverage.taxon.item' /></a></div>
			</div>
		</#if>


		
	</div>
	<div class="addNew"><a id="plus" href=""><@s.text name='manage.metadata.addnew' /> <@s.text name='manage.metadata.taxcoverage.item' /></a></div>

	<div class="buttons">
		<@s.submit cssClass="button" name="save" key="button.save"/>
		<@s.submit cssClass="button" name="cancel" key="button.cancel"/>
	</div>
	<!-- internal parameter -->
	<input name="r" type="hidden" value="${resource.shortname}" />
</form>
</div>



<!-- The base form that is going to be cloned every time an user click on the 'add' link -->
<!-- The next divs are hidden. -->
<div id="baseItem" class="item clearfix" style="display:none">
	<div class="right">
		<a id="removeLink" class="removeLink" href="">[ <@s.text name='manage.metadata.removethis'/> <@s.text name='manage.metadata.taxcoverage.item'/> ]</a>
	</div>

	<@text i18nkey="eml.taxonomicCoverages.description" help="i18n" name="description" requiredField=true />

	<!-- Taxon list-->
	<div class="addNew"><a id="taxonsLink" class="show-taxonList" href="" ><@s.text name='manage.metadata.taxcoverage.addSeveralTaxa' /></a></div>
	<div id="list" class="half clearfix" style="display:none">
		<@text i18nkey="eml.taxonomicCoverages.taxonList" help="i18n" name="taxon-list" value="" />
		<div class="buttons taxon-list">
			<@s.submit cssClass="button" name="add-button" key="button.add"/>
		</div>
	</div>
	<div id="subItems"></div>
	<div class="addNew"><a id="plus-subItem" href="" ><@s.text name='manage.metadata.addnew' /> <@s.text name='manage.metadata.taxcoverage.taxon.item' /></a></div>
</div>
<div id="subItem-9999" class="sub-item grid_17 suffix_1 clearfix" style="display:none">
	<div class="third">
		<@input i18nkey="eml.taxonomicCoverages.taxonKeyword.scientificName" name="scientificName" />
		<@input i18nkey="eml.taxonomicCoverages.taxonKeyword.commonName" name="commonName"  />
		<@select i18nkey="eml.taxonomicCoverages.taxonKeyword.rank"  name="rank" options=ranks  />
		<img id="trash" class="trash-icon" src="${baseURL}/images/trash-m.png">
	</div>
</div>

<#include "/WEB-INF/pages/inc/footer.ftl">
</#escape>
