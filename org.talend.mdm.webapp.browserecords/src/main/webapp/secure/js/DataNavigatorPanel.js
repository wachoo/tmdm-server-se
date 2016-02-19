amalto.namespace("amalto.itemsbrowser");

amalto.itemsbrowser.NavigatorPanel = function () {
	var width = 500;
	var height = 500;
	var svg = d3.select("#navigator").append("svg").attr("width",width).attr("height",height);
	
	var nodes = [ { name: "Product"}, { name: "Product1" },
	              { name: "Product2" }, { name: "Product3" },
	              { name: "ProductFamily" }, { name: "ProductFamily1" } ,
	              { name: "ProductFamily2" }, { name: "ProductFamily3" }];
	 
	 var links = [ { source : 0 , target: 1 } , { source : 0 , target: 2 } ,
	               { source : 0 , target: 3 } , { source : 2 , target: 4 } ,
	               { source : 4 , target: 5 } , { source : 4 , target: 6 },{ source : 4 , target: 7 } ];
	 
	 var force = d3.layout.force().nodes(nodes).links(links).size([width,height]).linkDistance(150).charge([-400]);
	 force.start();

	var svg_links = svg.selectAll("line").data(links).enter().append("line").style("stroke","#ccc").style("stroke-width",1);
	var color = d3.scale.category20();
	var svg_nodes = svg.selectAll("circle").data(nodes).enter().append("circle").attr("r",20).style("fill",function(d,i) {return color(i);}).each(function(d,i){
		d3.select(this).call(force.drag);
		d3.select(this).on("click", function(){
			if (i==0) {
				
			} else if (i==1) {
				amalto.navigator.Navigator.openRecord("1", "Product");
				return true;
			} else if (i==2) {
				amalto.navigator.Navigator.openRecord("2", "Product");
				return true;
			} else if (i==3) {
				amalto.navigator.Navigator.openRecord("3", "Product");
				return true;
			} else if (i==4) {
				
			} else if (i==5) {
				amalto.navigator.Navigator.openRecord("1", "ProductFamily");
				return true;
			} else if (i==6) {
				amalto.navigator.Navigator.openRecord("2", "ProductFamily");
				return true;
			} else if (i==7) {
				amalto.navigator.Navigator.openRecord("3", "ProductFamily");
				return true;
			}
		});						
	});
	var svg_texts = svg.selectAll("text").data(nodes).enter().append("text").style("fill","black").attr("dx",20).attr("dy",8).text(function(d){return d.name;});

	force.on("tick",function(){
		svg_links.attr("x1",function(d){return d.source.x;})
		.attr("y1",function(d){return d.source.y;})
		.attr("x2",function(d){return d.target.x;})
		.attr("y2",function(d){return d.target.y;})
		svg_nodes.attr("cx",function(d){return d.x;})
		.attr("cy",function(d){return d.y});
		svg_texts.attr("x",function(d){return d.x;})
		.attr("y",function(d){return d.y});
	});
}