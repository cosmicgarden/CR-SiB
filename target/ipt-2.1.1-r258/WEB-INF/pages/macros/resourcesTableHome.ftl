<#--
resourcesTable macro: Generates a data table that has searching, pagination, and sortable columns.
- shownPublicly: Whether the table will be shown publicly, or only internally to managers
- numResourcesShown: The number of resources shown in the table
- sEmptyTable: The message shown when there are no resource records in the table
- columnToSortOn: The column to sort on by default (index starting at 0)
- sortOrder: The sort order of the columnToSortOn
-->
<#macro resourcesTable shownPublicly numResourcesShown sEmptyTable columnToSortOn sortOrder>
<script type="text/javascript" charset="utf-8">
    
    var arrData = new Array();
    <#assign numArr=resources?size>
    
    <#list resources as s>
      var rowData = new Array();
      <#if s.eml.additionalInfo??>
        var admt='${s.eml.additionalInfo}';
        var texta="";
        var aut=admt.indexOf('[autoridad]')+'[autoridad]'.length;
        var num=admt.indexOf('[numero]')+'[numero]'.length;
        var hol=admt.indexOf('[titular]')+'[titular]'.length;
        var nit=admt.indexOf('[nit o cedula]')+'[nit o cedula]'.length;
        var dte=admt.indexOf('[fecha]')+'[fecha]'.length;
        if(admt.charAt(aut)!='['){
          rowData[0]=admt.substring(aut, admt.indexOf('[numero]'));
        }
        if(admt.charAt(num)!='['){
          rowData[1]=admt.substring(num, admt.indexOf('[titular]'));
        }
        if(admt.charAt(hol)!='['){
          rowData[2]=admt.substring(hol, admt.indexOf('[nit o cedula]'));
        }
        if(admt.charAt(nit)!='['){
          rowData[3]=admt.substring(nit, admt.indexOf('[fecha]'));
        }
        if(dte < admt.length){
          rowData[4]=admt.substring(dte, admt.length);
        }
        arrData.push(rowData);
      <#else>
        rowData[0]='--';
        rowData[1]='--';
        rowData[2]='--';
        rowData[3]='--';
        rowData[4]='--';
        arrData.push(rowData);
      </#if>
    </#list>

    <#if authority??>
        <#assign numAuth=authority?size>
        
    <#else>
        
    </#if>

    <#if resources??>
        
        <#assign numRes=resources?size>
        
        
    <#else>
        
    </#if>

    <#if number??>
        
        <#assign numNum=number?size>
        
        
    <#else>
        
    </#if>

    <#if holder??>
        
        <#assign numHol=number?size>
        
        
    <#else>
        
    </#if>

    <#assign emptyString="--">
    <#assign dotDot="..">

    /* resources list */
    var aDataSet = [
      <#list resources as r>
          ["<a href='${baseURL}/resource.do?r=${r.shortname}'><if><#if r.title?has_content>${r.title?replace("\'", "\\'")?replace("\"", '\\"')}<#else>${r.shortname}</#if></a>",
          <#if r.eml.additionalInfo?has_content>arrData[${r_index}][2]<#else>'${emptyString}'</#if>,
          <#if r.eml.additionalInfo?has_content>arrData[${r_index}][3]<#else>'${emptyString}'</#if>,
          <#if r.eml.additionalInfo?has_content>arrData[${r_index}][1]<#else>'${emptyString}'</#if>,
            <#if dateCert??>'${dateCert[r_index]}'<#else>'${emptyString}'</#if>,
           <#if authority??>'${authority[r_index]}'<#else>'${emptyString}'</#if>,
           '${r.recordsPublished!0}',]<#if r_has_next>,</#if>
      </#list>
    ];

    $(document).ready(function() {
        $('#rtableContainer').html( '<table cellpadding="3" cellspacing="3" border="0" class="display" id="rtableHome"></table>' );
        $('#rtableHome').dataTable( {
            "aaData": aDataSet,
            "iDisplayLength": ${numResourcesShown},
            "bLengthChange": false,
            "bAutoWidth": false,
            "oLanguage": {
                "sEmptyTable": "<@s.text name="${sEmptyTable}"/>",
                "sZeroRecords": "<@s.text name="dataTables.sZeroRecords"/>",
                "sInfo": "<@s.text name="dataTables.sInfo"/>",
                "sInfoEmpty": "<@s.text name="dataTables.sInfoEmpty"/>",
                "sInfoFiltered": "<@s.text name="dataTables.sInfoFiltered"/>",
                "sSearch": "<@s.text name="manage.mapping.filter"/>:",
                "oPaginate": {
                    "sNext": "<@s.text name="pager.next"/>",
                    "sPrevious": "<@s.text name="pager.previous"/>"

                }
            },
            "aoColumns": [
                { "sTitle": "<@s.text name="dataTables.portal.name"/>"},
                { "sTitle": "<@s.text name="dataTables.portal.holder"/>"},
                { "sTitle": "<@s.text name="dataTables.portal.ind"/>"},
                { "sTitle": "<@s.text name="dataTables.portal.number"/>"},
                { "sTitle": "<@s.text name="dataTables.manage.certidate"/>"},
                { "sTitle": "<@s.text name="dataTables.portal.auto"/>"},
                { "sTitle": "<@s.text name="dataTables.portal.records"/>"}, 
            ]
        } );
        $('#rtableHome').dataTable().columnFilter({ sPlaceHolder: "head:after",
                                                    aoColumns: [
                                                                { type: "text" },
                                                                { type: "text" },
                                                                { type: "text" },
                                                                { type: "text" },
                                                                { type: "text" },
                                                                { type: "text" },
                                                                { type: "text" }
                                                                ]
        });
    } );
</script>
</#macro>