/*
 * @include  "/com.amalto.webapp.core/web/secure/ext.ux/DWRProxy.js"
 * @include  "/com.amalto.webapp.core/web/secure/js/core.js"
 */
Ext.namespace('amalto.updatereport');
amalto.updatereport.UpdateReportPanel = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.updatereport.UpdateReportPanel.superclass.constructor.call(this);
	loadResource("/updatereport/secure/js/UpdateReportTimeLinePanel.js", "");
};

Ext.override(Ext.menu.Menu, {
    autoWidth: function() {
        this.width += "px";
    }
}); 

var searchCriteria;
var searchStart;
var searchLimit;

amalto.updatereport.PagingToolbar = Ext.extend(Ext.PagingToolbar, {
	updateInfo : function(){
		if (amalto.updatereport.PagingToolbar.pagingAccurate){
			this.displayMsg = amalto.updatereport.bundle.getMsg("displayMsg");
		} else {
			this.displayMsg = amalto.updatereport.bundle.getMsg("displayMsg~");
		}
		amalto.updatereport.PagingToolbar.superclass.updateInfo.call(this);
	}
});
amalto.updatereport.PagingToolbar.pagingAccurate = true;

Ext
		.extend(
				amalto.updatereport.UpdateReportPanel,
				Ext.Panel,
				{
					criteria : "",
					initUIComponents : function() {

						Ext
								.apply(
										Ext.form.VTypes,
										{
											dateRange : function(val, field) {
												if (field.dateRange) {
													var beginId = field.dateRange.begin;
													this.beginField = Ext
															.getCmp(beginId);
													var endId = field.dateRange.end;
													this.endField = Ext
															.getCmp(endId);
													var beginDate = this.beginField
															.getValue();
													var endDate = this.endField
															.getValue();
												}

												if (beginDate != ""
														&& endDate != "") {
													if (beginDate <= endDate) {
														return true;
													} else {
														return false;
													}
												}

												return true;
											},
											dateRangeText : 'Start Data can not be greater than End Date '
										});

						this.recordType = Ext.data.Record.create([

						{
							name : "dataCluster",
							type : "string"
						}, {
							name : "dataModel",
							type : "string"
						}, {
							name : "concept",
							type : "string"
						}, {
							name : "key",
							type : "string"
						}, {
							name : "revisionID",
							type : "string"
						}, {
							name : "operationType",
							type : "string"
						}, {
							name : "timeInMillis",
							type : "string"
						}, {
							name : "epochTime",
							type : "string"
						}, {
							name : "source",
							type : "string"
						}, {
							name : "userName",
							type : "string"
						}, {
							name : "ids",
							type : "string"
						}

						]);

						this.store1 = new Ext.data.Store({
							proxy : new Ext.data.DWRProxy(
									UpdateReportInterface.getUpdateReportList,
									true, function(listRange){
										amalto.updatereport.PagingToolbar.pagingAccurate = listRange.pagingAccurate;
									}),
							reader : new Ext.data.ListRangeReader({
								id : 'keys',
								totalProperty : 'totalSize',
								root : 'data'
							}, this.recordType),
							remoteSort : true
						});

						this.store1.on('beforeload', function(button, event) {
							this.onBeforeloadStore();
						}.createDelegate(this));

						this.store1.on('load', function(button, event) {
							if (button.lastOptions != null) {
								searchStart = button.lastOptions.params.start;
								searchLimit = button.lastOptions.params.limit;
								Ext.getCmp("updateReportPagingToolbar").loading
										.setIconClass("x-tbar-done");
							}
						});
						this.timelinePanel = new Ext.Panel(
								{
									id : "timelinePanel",
									title : amalto.updatereport.bundle.getMsg('TIMELINE_TITLE'),
									iconCls : "report_table_timeline",
									autoScroll : true,
									titleCollapse : true,
									layout : "fit",
									html : "<div id='tl' class='timeline-default' style='height:464px;'></div>",
									listeners : {
										bodyresize : function(p, width, height) {
											var obj = Ext.get('tl');
											obj.setHeight(height);
											if (!timeLinePanelHeight) {
												timeLinePanelHeight = height;
											} else {
												timeLinePanelHeight = height;
												document.getElementById("tl").innerHTML = "";
												renderTimeline(
														jsonData4Timeline,
														initDate4Timeline);
											}
										}
									}
								});

						this.gridPanel1 = new Ext.grid.GridPanel(
								{
									id : "updateReportGridPanel",
									title : amalto.updatereport.bundle.getMsg('GRID_TITLE'),
									iconCls : "report_tab_table",
									store : this.store1,
									border : false,
									loadMask : true,
									layout : "fit",
									selModel : new Ext.grid.RowSelectionModel(
											{}),
									columns : [
											{
												hidden : false,
												header : amalto.updatereport.bundle
														.getMsg("dataCluster"),
												dataIndex : "dataCluster",
												sortable : true
											},
											{
												hidden : false,
												header : amalto.updatereport.bundle
														.getMsg("dataModel"),
												dataIndex : "dataModel",
												sortable : true
											},
											{
												hidden : false,
												header : amalto.updatereport.bundle
														.getMsg("concept"),
												dataIndex : "concept",
												sortable : true
											},
											{
												hidden : false,
												header : amalto.updatereport.bundle
														.getMsg("key"),
												dataIndex : "key",
												sortable : true
											},
											{
												hidden : false,
												header : amalto.updatereport.bundle
														.getMsg("revisionID"),
												dataIndex : "revisionID",
												sortable : true
											},
											{
												hidden : false,
												header : amalto.updatereport.bundle
														.getMsg("operationType"),
												dataIndex : "operationType",
												sortable : true
											},
											{
												hidden : false,
												header : amalto.updatereport.bundle
														.getMsg("timeInMillis"),
												dataIndex : "timeInMillis",
												sortable : true
											},
											{
												hidden : false,
												header : amalto.updatereport.bundle
														.getMsg("source"),
												dataIndex : "source",
												sortable : true
											},
											{
												hidden : false,
												header : amalto.updatereport.bundle
														.getMsg("userName"),
												dataIndex : "userName",
												sortable : true
											} ],
									listeners : {
										'rowdblclick' : function(grid,
												rowIndex, e) {

											var record = grid.getStore().getAt(
													rowIndex);
											var ids = record.data.ids;

											var tabPanel = amalto.core
													.getTabPanel();
											var dataLogViewer = tabPanel
													.getItem(ids);
											if (dataLogViewer == undefined) {
												UpdateReportInterface
														.isEnterpriseVersion(function(
																data) {
															if (!data) {
																dataLogViewer = new amalto.updatereport.DataLogViewer(
																		{
																			'ids' : ids,
																			'key' : record.data.key,
																			'operationType' : record.data.operationType,
																			'concept' : record.data.concept,
																			'dataCluster' : record.data.dataCluster,
																			'dataModel' : record.data.dataModel
																		});
																tabPanel
																.add(dataLogViewer);
														dataLogViewer
																.show();
														dataLogViewer
																.doLayout();
														amalto.core
																.doLayout();
															} else {
																// Note: this
																// feature is
																// only enabled
																// in enterprise
																// version
																UpdateReportInterface.isJournalHistoryExist(record.data.dataCluster,record.data.dataModel,record.data.concept,record.data.key,record.data.operationType,record.data.epochTime,function(data) {
																	if (data) {
																		dataLogViewer = new amalto.updatereport.HistoryViewer(
																		{
																			'ids' : ids,
																			'date' : record.data.epochTime,
																			'key' : record.data.key,
																			'operationType' : record.data.operationType,
																			'concept' : record.data.concept,
																			'dataCluster' : record.data.dataCluster,
																			'dataModel' : record.data.dataModel
																		});
																		tabPanel
																		.add(dataLogViewer);
																dataLogViewer
																		.show();
																dataLogViewer
																		.doLayout();
																amalto.core
																		.doLayout();
																	}																	
																});
															}															
														});
											} else {
												dataLogViewer.show();
												dataLogViewer.doLayout();
												amalto.core.doLayout();
											}
										}
									},
									bbar : new amalto.updatereport.PagingToolbar(
											{
												id : "updateReportPagingToolbar",
												displayMsg : amalto.updatereport.bundle
														.getMsg("displayMsg"),
												displayInfo : true,
												store : this.store1,
												xtype : "paging",
												emptyMsg : amalto.updatereport.bundle
														.getMsg("emptyMsg"),
												pageSize : parseInt(this.getCookie('updateReportPaging_pageSize')),
												items : [
														new Ext.Toolbar.Separator(),
														new Ext.Toolbar.TextItem(
																amalto.updatereport.bundle
																		.getMsg("lines_per_page")
																		+ " : "),
														new Ext.form.NumberField(
																{
																	id : 'updateRLineMaxItems',
																	value : parseInt(this.getCookie('updateReportPaging_pageSize')),
																	width : 30,
																	validator : function(v){
																		var vi = parseInt(v);
																		if (v == vi){
																			return true;
																		}
																		return false;
																	},
																	listeners : {
																		'specialkey' : function(
																				a,
																				e) {
																			if (e
																					.getKey() == e.ENTER && a.isValid(true)) {
																				var lineMax = DWRUtil
																						.getValue('updateRLineMaxItems');
																				if (lineMax == null
																						|| lineMax == "")
																					lineMax = this
																							.getCookie('updateReportPaging_pageSize');
																				Ext
																						.getCmp("updateReportPagingToolbar").pageSize = parseInt(lineMax);
																				Ext
																						.getCmp("updateReportGridPanel").store
																						.reload({
																							params : {
																								start : 0,
																								limit : lineMax
																							}
																						});
																				this
																						.setCookie(
																								'updateReportPaging_pageSize',
																								lineMax);
																			}
																		}
																				.createDelegate(this),
																		'change' : function(
																				field,
																				newValue,
																				oldValue) {
																			if (newValue != oldValue) {
																				lineMax = newValue;
																				if (lineMax == null
																						|| lineMax == "")
																					lineMax = this
																							.getCookie('updateReportPaging_pageSize');
																				Ext
																						.getCmp("updateReportPagingToolbar").pageSize = parseInt(lineMax);
																				Ext
																						.getCmp("updateReportGridPanel").store
																						.reload({
																							params : {
																								start : 0,
																								limit : lineMax
																							}
																						});
																				this
																						.setCookie(
																								'updateReportPaging_pageSize',
																								lineMax);
																			}
																		}
																				.createDelegate(this)
																	}
																}) ]
											})
								});

						this.sourceStore = new Ext.data.Store({
							proxy : new Ext.data.MemoryProxy([
									[ 'genericUI', 'genericUI' ],
									[ 'adminWorkbench', 'adminWorkbench' ],
									[ 'dataSynchronization',
											'dataSynchronization' ],
									[ 'workflow', 'workflow' ] ]),
							reader : new Ext.data.ArrayReader({}, [ {
								name : 'value',
								mapping : 0,
								type : 'string'
							}, {
								name : 'text',
								mapping : 1
							} ]),
							autoLoad : true
						});

						this.operationTypeStore = new Ext.data.Store({
							proxy : new Ext.data.MemoryProxy([
									[ 'CREATE', 'CREATE' ],
									[ 'UPDATE', 'UPDATE' ],
									[ 'PHYSICAL_DELETE', 'PHYSICAL_DELETE' ],
									[ 'LOGIC_DELETE', 'LOGIC_DELETE' ],
									[ 'RESTORED', 'RESTORED' ],
									[ 'ACTION', 'ACTION' ] ]),
							reader : new Ext.data.ArrayReader({}, [ {
								name : 'value',
								mapping : 0,
								type : 'string'
							}, {
								name : 'text',
								mapping : 1
							} ]),
							autoLoad : true
						});

						this.tabPanel8 = new Ext.TabPanel({
							id : "tabPanel",
							region : "center",
							activeTab : 0,
							items : [ this.gridPanel1, this.timelinePanel ],
							listeners : {
								"tabchange" : function(obj, tab) {
									if (tab.getId() == "timelinePanel") {
										UpdateReportInterface.getReportString(
												searchStart, searchLimit,
												searchCriteria, language,
												callback);
									}
								}
							}
						});

						Ext
								.apply(
										this,
										{
											layout : "border",

											title : amalto.updatereport.bundle
													.getMsg("title"),
											items : [
													{
														frame : false,
														height : 180,
														layout : "fit",
														split : false,
														collapsible : true,
														title : amalto.updatereport.bundle
																.getMsg("searchPanel_tile"),
														border : false,
														items : [ {
															height : 30,
															layout : "column",
															items : [
																	{
																		columnWidth : ".5",
																		layout : "form",
																		items : [
																				{
																					name : "concept",
																					fieldLabel : amalto.updatereport.bundle
																							.getMsg("concept"),
																					xtype : "textfield",
																					listeners : {
																						'specialkey' : function(
																								field,
																								event) {
																							this
																									.onSearchKeyClick(
																											field,
																											event);
																						}
																								.createDelegate(this)
																					}
																				},
																				{
																					name : "source",
																					// emptyText
																					// :
																					// "Select
																					// a
																					// source...",
																					fieldLabel : amalto.updatereport.bundle
																							.getMsg("source"),
																					xtype : "combo",
																					store : this.sourceStore,
																					displayField : 'text',
																					valueField : 'value',
																					typeAhead : true,
																					triggerAction : 'all',
																					mode : 'local',
																					listeners : {
																						'specialkey' : function(
																								field,
																								event) {
																							this
																									.onSearchKeyClick(
																											field,
																											event);
																						}
																								.createDelegate(this)
																					}
																				},
																				{
																					id : "startDate",
																					name : "startDate",
																					fieldLabel : amalto.updatereport.bundle
																							.getMsg("start_date"),
																					xtype : "datefield",
																					format : "Y-m-d H:i:s",
																					width : 150,
																					invalidText: amalto.updatereport.bundle.getMsg("Error_Date_Field"),
																					readOnly : false,
																					listeners : {
																						'specialkey' : function(
																								field,
																								event) {
																							this
																									.onSearchKeyClick(
																											field,
																											event);
																						}
																								.createDelegate(this)
																					},
																					vtype : 'dateRange',
																					dateRange : {
																						begin : 'startDate',
																						end : 'endDate'
																					}
																				} ],
																		border : false
																	},
																	{
																		columnWidth : ".5",
																		layout : "form",
																		items : [
																				{
																					name : "key",
																					fieldLabel : amalto.updatereport.bundle
																							.getMsg("key"),
																					xtype : "textfield",
																					listeners : {
																						'specialkey' : function(
																								field,
																								event) {
																							this
																									.onSearchKeyClick(
																											field,
																											event);
																						}
																								.createDelegate(this)
																					}
																				},
																				{
																					name : "operationType",
																					// emptyText
																					// :
																					// "Select
																					// a
																					// type...",
																					fieldLabel : amalto.updatereport.bundle
																							.getMsg("operationType"),
																					xtype : "combo",
																					store : this.operationTypeStore,
																					displayField : 'text',
																					valueField : 'value',
																					typeAhead : true,
																					triggerAction : 'all',
																					mode : 'local',
																					listeners : {
																						'specialkey' : function(
																								field,
																								event) {
																							this
																									.onSearchKeyClick(
																											field,
																											event);
																						}
																								.createDelegate(this)
																					}
																				},
																				{
																					id : "endDate",
																					name : "endDate",
																					fieldLabel : amalto.updatereport.bundle
																							.getMsg("end_date"),
																					xtype : "datefield",
																					format : "Y-m-d H:i:s",
																					width : 150,
																					invalidText: amalto.updatereport.bundle.getMsg("Error_Date_Field"),
																					readOnly : false,
																					listeners : {
																						'specialkey' : function(
																								field,
																								event) {
																							this
																									.onSearchKeyClick(
																											field,
																											event);
																						}
																								.createDelegate(this)
																					},
																					vtype : 'dateRange',
																					dateRange : {
																						begin : 'startDate',
																						end : 'endDate'
																					}
																				} ],
																		border : false
																	} ],
															border : false
														} ],
														region : "north",
														bodyStyle : 'padding:5px',
														buttons : [
																{
																	handler : function(
																			button,
																			event) {
																		this
																				.onResetBtnClick(
																						button,
																						event);
																	}
																			.createDelegate(this),
																	text : amalto.updatereport.bundle
																			.getMsg("reset")
																},
																{
																	handler : function(
																			button,
																			event) {
																		this
																				.onSearchBtnClick(
																						button,
																						event);
																	}
																			.createDelegate(this),
																	text : amalto.updatereport.bundle
																			.getMsg("search")
																},
																{
																	handler : function() {
																		var curcriteria = this
																				.getRequestParam();
																		window.location.href = "/updatereport/secure/updateReportDetails?" + Ext.urlEncode({params:curcriteria, language:language})
																	}
																			.createDelegate(this),
																	text : amalto.updatereport.bundle
																			.getMsg("export")
																} ]
													}, this.tabPanel8 ],
											id : "UpdateReportPanel",
											closable : true,
											border : false
										});
					},

					initListData : function(itemsBroswer) {
						this.isItemsBrowser = itemsBroswer;
						this.store1
								.load({
									params : {
										start : 0,
										limit : this
												.getCookie('updateReportPaging_pageSize')
									}
								});
					},

					isItemsBrowser : false,

					doSearchList : function(itemsBrowser) {
						this.isItemsBrowser = itemsBrowser;
						var pageSize = Ext.getCmp("updateReportPagingToolbar").pageSize;
						this.store1.reload({
							params : {
								start : 0,
								limit : pageSize
							}
						});
						searchStart = 0;
						searchLimit = pageSize;
						UpdateReportInterface
								.getReportString(searchStart, searchLimit,
										searchCriteria, language, callback);
					},

					onSearchBtnClick : function(button, event) {

						this.doSearchList(false);

					},

					onSearchKeyClick : function(field, event) {

						if (event.getKey() == Ext.EventObject.ENTER) {
							this.doSearchList(false);
						}

					},

					setSearchCriteria : function(conceptValue, keyValue,
							sourceValue, operationTypeValue, startDateValue,
							endDateValue) {

						if (conceptValue != '')
							DWRUtil.setValue('concept', conceptValue);
						if (keyValue != '')
							DWRUtil.setValue('key', keyValue);
						if (sourceValue != '')
							DWRUtil.setValue('source', sourceValue);
						if (operationTypeValue != '')
							DWRUtil.setValue('operationType',
									operationTypeValue);
						if (startDateValue != '')
							DWRUtil.setValue('startDate', startDateValue);
						if (endDateValue != '')
							DWRUtil.setValue('endDate', endDateValue);

					},

					onResetBtnClick : function(button, event) {

						DWRUtil.setValue('concept', '');
						DWRUtil.setValue('key', '');
						DWRUtil.setValue('source', '');
						DWRUtil.setValue('operationType', '');
						DWRUtil.setValue('startDate', '');
	                    DWRUtil.setValue('endDate', '');
						this.criteria = "";
						searchCriteria = "";
					},

					getRequestParam : function(startDateOfTimeLine,endDateOfTimeLine) {
						var requestParam = "";

						var concept = DWRUtil.getValue('concept');
						if (concept != "")
							requestParam += ",concept:'" + concept + "'";
						var key = DWRUtil.getValue('key');
						if (key != "")
							requestParam += ",key:'" + key + "'";
						var source = DWRUtil.getValue('source');
						if (source != "")
							requestParam += ",source:'" + source + "'";
						var operationType = DWRUtil.getValue('operationType');
						if (operationType != "")
							requestParam += ",operationType:'" + operationType
									+ "'";
						var startDate = DWRUtil.getValue('startDate');					
						if (startDate != ""){
							if (typeof(startDateOfTimeLine) != "undefined" && startDateOfTimeLine != "" && startDateOfTimeLine > startDate){
								requestParam += ",startDate:'" + startDateOfTimeLine + "'"; 
							}else{
								requestParam += ",startDate:'" + startDate + "'";
							}		
						}else if (typeof(startDateOfTimeLine) != "undefined" && startDateOfTimeLine != ""){
							requestParam += ",startDate:'" + startDateOfTimeLine + "'";
						}												
						var endDate = DWRUtil.getValue('endDate');					
						if (endDate != ""){
							if (typeof(endDateOfTimeLine) != "undefined" && endDateOfTimeLine != "" && endDateOfTimeLine < endDate){
								requestParam += ",endDate:'" + endDateOfTimeLine + "'";
							}else{
								requestParam += ",endDate:'" + endDate + "'";
							}
							
						}else if (typeof(endDateOfTimeLine) != "undefined" && endDateOfTimeLine != ""){
							requestParam += ",endDate:'" + endDateOfTimeLine + "'";
						}							
						if (this.isItemsBrowser == true)
							requestParam += ",itemsBrowser:'"
									+ this.isItemsBrowser + "'";

						if (requestParam != "") {
							requestParam = requestParam.substring(1)
							requestParam = "{" + requestParam + "}";
						}

						return requestParam;
					},

					onBeforeloadStore : function() {
						this.criteria = this.getRequestParam();
						searchCriteria = this.criteria;
						Ext.apply(this.store1.baseParams, {
							regex : this.criteria
						});
					},
					
					refresh : function() {
						this.doSearchList(false);
					},

					getCookie : function(cookieName) {

						if (document.cookie.length > 0) {
							begin = document.cookie.indexOf(cookieName + "=");
							if (begin != -1) {
								begin += cookieName.length + 1;
								end = document.cookie.indexOf(";", begin);
								if (end == -1)
									end = document.cookie.length;
								return unescape(document.cookie.substring(
										begin, end));
							}
						}
						return 20;
					},

					setCookie : function(cookieName, cookieValue) {

						var expireDate = new Date();
						expireDate.setTime(expireDate.getTime()
								+ (100 * 24 * 3600 * 1000));
						document.cookie = cookieName + "="
								+ escape(cookieValue) + "; expires="
								+ expireDate.toGMTString();
					}
				});


