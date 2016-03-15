amalto.namespace("amalto.itemsbrowser");

amalto.itemsbrowser.NavigatorPanel = function(restServiceUrl, id, concept,
		cluster, viewPK) {
	var NAVIGATOR_NODE_IN_ENTITY_TYPE = 1;
	var NAVIGATOR_NODE_OUT_ENTITY_TYPE = 2;
	var NAVIGATOR_NODE_VALUE_TYPE = 3;
	var NAVIGATOR_NOD_COLOR_SELECTED = "rgb(255, 2, 42)";
	var NAVIGATOR_NOD_COLOR_INBOUND = "rgb(24, 2, 255)";
	var NAVIGATOR_NOD_COLOR_OUTBOUND = "rgb(2, 255, 145)";
	var NAVIGATOR_NOD_COLOR_DATA = "rgb(231, 253, 6)";
	var nodeList = [];
	var linkList = [];
	var width = 800;
	var height = 800;
	var zoom = d3.behavior.zoom().scaleExtent([ 1, 100 ]).on("zoom", zoomed);
	var svg = d3.select("#navigator").append("svg").attr("width", width).attr(
			"height", height).append("g").call(zoom);
    var rect = svg.append("rect")
    .attr("width", width)
    .attr("height", height)
    .style("fill", "none")
    .style("pointer-events", "all");

    var container = svg.append("g");
	var links;
	var nodes;
	var link;
	var node;
	var link_text;
	var node_text;
	var drag;
	var force;
	init(id, concept, cluster, viewPK);

	function paint() {
		link = link.data(links);
		node = node.data(nodes);
		link.enter().append("line").style(
				"stroke-width", 1).style("stroke", function(l){return getColor(l)});
		node.enter().append("circle").attr("r", 20).attr("identifier",function(d) {return getIdentifier(d)}).style("fill",function(d) {return getColor(d)}).each(function(d, i) {
			d3.select(this).call(drag);
			d3.select(this).on("click", click);
			d3.select(this).on("dblclick", dblclick);
			d3.select(this).on("mouseover", mouseover);
			d3.select(this).on("mouseout", mouseout);
		});

		link_text = link_text.data(links);
		link_text.enter().append("text").style("font-weight", "bold").style(
				"fill-opacity", "0.0").text(function(d) {
			return d.navigator_line_label;
		});
		node_text = node_text.data(nodes);
		node_text
				.enter()
				.append("text")
				.style("fill", "black")
				.attr("dx", 20)
				.attr("dy", 8)
				.text(
						function(d) {
							return d.navigator_node_label;
						});
		force.start();
	}

	function click(d, i) {

		node.style("fill", function(node) {
			if (d === node) {
				return NAVIGATOR_NOD_COLOR_SELECTED
			} else {
				return getColor(node);
			}
		});

		if (!d.navigator_node_expand) {
			if (NAVIGATOR_NODE_VALUE_TYPE == d.navigator_node_type) {
				Ext.Ajax
						.request({
							url : restServiceUrl + '/data/' + cluster
									+ '/outBoundTypes/',
							method : 'GET',
							params : {
								ids : d.navigator_node_ids,
								type : d.navigator_node_concept
							},
							success : function(response, options) {
								var newNodes = eval('(' + response.responseText
										+ ')');
								for ( var i = 0; i < newNodes.length; i++) {
									var node = newNodes[i];

									var newNode = {
										x : d.x + getRandomInt(-15, 15),
										y : d.y + getRandomInt(-15, 15),
										navigator_node_ids : node.navigator_node_ids,
										navigator_node_concept : node.navigator_node_concept,
										navigator_node_type : node.navigator_node_type,
										navigator_node_label : node.navigator_node_label,
										navigator_node_expand : false
									};
									nodes.push(newNode);
									var newLink = {
										source : d,
										target : newNode,
										navigator_node_type : node.navigator_node_type,
										navigator_line_label : node.navigator_line_label,
										navigator_node_concept : node.navigator_node_concept
									};
									links.push(newLink);
								}
								paint();
							},
							failure : function() {

							}
						});
				Ext.Ajax
						.request({
							url : restServiceUrl + '/data/' + cluster
									+ '/inBoundTypes/',
							method : 'GET',
							params : {
								ids : d.navigator_node_ids,
								type : d.navigator_node_concept
							},
							success : function(response, options) {
								var newNodes = eval('(' + response.responseText
										+ ')');
								for ( var i = 0; i < newNodes.length; i++) {
									var node = newNodes[i];

									var newNode = {
										x : d.x + getRandomInt(-15, 15),
										y : d.y + getRandomInt(-15, 15),
										navigator_node_concept : node.navigator_node_concept,
										navigator_node_foreignkey_path : node.navigator_node_foreignkey_path,
										navigator_node_foreignkey_value : node.navigator_node_foreignkey_value,
										navigator_node_type : node.navigator_node_type,
										navigator_node_label : node.navigator_node_label,
										navigator_node_expand : false
									};
									nodes.push(newNode);
									var newLink = {
										source : d,
										target : newNode,
										navigator_node_type : node.navigator_node_type,
										navigator_line_label : node.navigator_line_label,
										navigator_node_concept : node.navigator_node_concept
									};
									links.push(newLink);
								}
								paint();
							},
							failure : function() {

							}
						});
			} else {
				if (NAVIGATOR_NODE_IN_ENTITY_TYPE == d.navigator_node_type) {
					Ext.Ajax
					.request({
						url : restServiceUrl + '/data/' + cluster
								+ '/inBoundRecords/',
						method : 'GET',
						params : {
							type : d.navigator_node_concept,
							foreignKeyPath : d.navigator_node_foreignkey_path,
							foreignKeyValue : d.navigator_node_foreignkey_value
						},
						success : function(response, options) {
							var newNodes = eval('(' + response.responseText
									+ ')');
							for ( var i = 0; i < newNodes.length; i++) {
								var node = newNodes[i];

								var newNode = {
									x : d.x + getRandomInt(-15, 15),
									y : d.y + getRandomInt(-15, 15),
									navigator_node_ids : node.navigator_node_ids,
									navigator_node_concept : node.navigator_node_concept,
									navigator_node_type : node.navigator_node_type,
									navigator_node_label : node.navigator_node_label,
									navigator_node_expand : false
								};
								nodes.push(newNode);
								var newLink = {
									source : d,
									target : newNode,
									navigator_node_type : node.navigator_node_type,
									navigator_line_label : node.navigator_line_label,
									navigator_node_concept : node.navigator_node_concept
								};
								links.push(newLink);
							}
							paint();
						},
						failure : function() {

						}
					});
				} else {
					Ext.Ajax
					.request({
						url : restServiceUrl + '/data/' + cluster
								+ '/records/',
						method : 'GET',
						params : {
							type : d.navigator_node_concept,
							ids : d.navigator_node_ids
						},
						success : function(response, options) {
							var newNodes = eval('(' + response.responseText
									+ ')');
							for ( var i = 0; i < newNodes.length; i++) {
								var node = newNodes[i];

								var newNode = {
									x : d.x + getRandomInt(-15, 15),
									y : d.y + getRandomInt(-15, 15),
									navigator_node_ids : node.navigator_node_ids,
									navigator_node_concept : d.navigator_node_concept,
									navigator_node_type : node.navigator_node_type,
									navigator_node_label : node.navigator_node_label,
									navigator_node_expand : false
								};
								nodes.push(newNode);
								var newLink = {
									source : d,
									target : newNode,
									navigator_node_type : node.navigator_node_type,
									navigator_line_label : node.navigator_line_label,
									navigator_node_concept : node.navigator_node_concept
								};
								links.push(newLink);
							}
							paint();
						},
						failure : function() {

						}
					});
				}

			}
			d.navigator_node_expand = true;
		}
		if (NAVIGATOR_NODE_VALUE_TYPE == d.navigator_node_type) {
			amalto.navigator.Navigator.openRecord(d.navigator_node_ids,
					d.navigator_node_concept);
		}
	}

	var dblclick = function(d, i) {
		d.fixed = false;
	}

	var getRandomInt = function(min, max) {
		return Math.floor(Math.random() * (max - min + 1) + min);
	}

	function mouseover(d, i) {
		link_text.style("fill-opacity", function(link) {
			if (link.source === d || link.target === d) {
				return 1.0;
			} else {
				return 0.0;
			}
		});
		d3.select(this).transition()
        .duration(750)
        .attr("r", "35");
	}

	function mouseout(d, i) {
		link_text.style("fill-opacity", "0.0");
		 d3.select(this).transition()
         .duration(750)
         .attr("r", "20");
	}

	function tick() {
		link.attr("x1", function(d) {
			return d.source.x;
		}).attr("y1", function(d) {
			return d.source.y;
		}).attr("x2", function(d) {
			return d.target.x;
		}).attr("y2", function(d) {
			return d.target.y;
		})
		node.attr("cx", function(d) {
			return d.x;
		}).attr("cy", function(d) {
			return d.y
		});
		link_text.attr("x", function(d) {
			return (d.source.x + d.target.x) / 2;
		}).attr("y", function(d) {
			return (d.source.y + d.target.y) / 2;
		});
		node_text.attr("x", function(d) {
			return d.x;
		}).attr("y", function(d) {
			return d.y
		});
	}

	function zoomed() {
		svg.attr("transform", "translate(" + d3.event.translate + ")scale("
				+ d3.event.scale + ")");
	}

	function init(id, concept, cluster, viewPK) {
		var ids = new Array(id);
		Ext.Ajax.request({
			url : restServiceUrl + '/data/' + cluster + '/records/',
			method : 'GET',
			params : {
				type : concept,
				ids : ids
			},
			success : function(response, options) {
				nodes = eval('(' + response.responseText + ')');
				links = [];
				force = d3.layout.force().nodes(nodes).links(links).size(
						[ width, height ]).linkDistance(150).charge([ -400 ]);
				 drag = force.drag().on("dragstart", function(d, i) {
				 d.fixed = true;
				 });

				links = force.links();
				nodes = force.nodes();
				link = container.append("g").selectAll(".link");
				node = container.append("g").selectAll(".node");
				link_text = container.append("g").selectAll(".linetext");
				node_text = container.append("g").selectAll(".nodetext");
				force.on("tick", tick);
				paint();
			},
			failure : function() {

			}
		});
	}
	
	function getColor(o) {
		if (NAVIGATOR_NODE_IN_ENTITY_TYPE == o.navigator_node_type) {
			return NAVIGATOR_NOD_COLOR_INBOUND;
		} else if (NAVIGATOR_NODE_OUT_ENTITY_TYPE == o.navigator_node_type) {
			return NAVIGATOR_NOD_COLOR_OUTBOUND;
		} else {
			return NAVIGATOR_NOD_COLOR_DATA;
		}
	}
	
	function getIdentifier(o) {
		var ids = o.navigator_node_ids;
		var concept = o.navigator_node_concept;
		return 'navigator_' + o.navigator_node_concept + '_' + o.navigator_node_ids;
	}
}