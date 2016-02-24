amalto.namespace("amalto.itemsbrowser");

amalto.itemsbrowser.NavigatorPanel = function() {
	
	var nodeList = [ {
		name : "Product1",
		id : "Product1",
		concept : "Product"
	}, {
		name : "Product2",
		id : "Product2",
		concept : "Product"
	}, {
		name : "Product3",
		id : "Product3",
		concept : "Product"
	}, {
		name : "ProductFamily1",
		id : "ProductFamily1",
		concept : "ProductFamily"
	}, {
		name : "ProductFamily2",
		id : "ProductFamily2",
		concept : "ProductFamily"
	}, {
		name : "ProductFamily3",
		id : "ProductFamily3",
		concept : "ProductFamily"
	} ];

	var linkList = [ {
		source : 0,
		target : 3,
		concept : "ProductFamily"
	}, {
		source : 0,
		target : 4,
		concept : "ProductFamily"
	}, {
		source : 0,
		target : 5,
		concept : "ProductFamily"
	}, {
		source : 4,
		target : 1,
		concept : "Product"
	}, {
		source : 4,
		target : 2,
		concept : "Product"
	} ];

	var width = 800;
	var height = 800;
	var zoom = d3.behavior.zoom().scaleExtent([ 1, 100 ]).on("zoom", zoomed);
	var svg = d3.select("#navigator").append("svg").attr("width", width).attr(
			"height", height).append("g").call(zoom);
	var force = d3.layout.force().nodes(nodeList).links(linkList).size(
			[ width, height ]).linkDistance(150).charge([ -400 ]);
	var drag = force.drag().on("dragstart", function(d, i) {
		d.fixed = true;
	});

	var links = force.links();
	var nodes = force.nodes();
	var link = svg.selectAll(".link");
	var node = svg.selectAll(".node");
	var link_text = svg.selectAll(".linetext");
	var node_text = svg.selectAll(".nodetext");
	force.on("tick", tick);
	paint();
	
	function paint() {
		link = link.data(links);
		link.enter().append("line").style("stroke", "#ccc").style(
				"stroke-width", 1);

		node = node.data(nodes);
		node.enter().append("circle").attr("r", 20).style("fill",
				"rgb(174, 199, 232)").each(function(d, i) {
			d3.select(this).call(drag);
			d3.select(this).on("click", click);
			d3.select(this).on("dblclick", dblclick);
			d3.select(this).on("mouseover", mouseover);
			d3.select(this).on("mouseout", mouseout);
		});

		link_text = link_text.data(links);
		link_text.enter().append("text").style("font-weight", "bold").style(
				"fill-opacity", "0.0").text(function(d) {
			return d.concept;
		});
		node_text = node_text.data(nodes);
		node_text.enter().append("text").style("fill", "black").attr("dx", 20)
				.attr("dy", 8).text(function(d) {
					return d.name;
				});
		force.start();
	}

	function click(d, i) {
		node.style("fill", function(node) {
			if (d === node) {
				return "rgb(31, 119, 180)";
			} else {
				return "rgb(174, 199, 232)";
			}
		});

		var newNode = {
			x : d.x + getRandomInt(-15, 15),
			y : d.y + getRandomInt(-15, 15),
			concept : "ProductFamily",
			id : "4"
		};
		nodes.push(newNode);
		var newLink = {
			source : d,
			target : newNode,
			concept : "ProductFamily",
			id : "4"
		};
		links.push(newLink);
		atomClicked = d;
		paint();
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
	}

	function mouseout(d, i) {
		link_text.style("fill-opacity", "0.0");
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

}