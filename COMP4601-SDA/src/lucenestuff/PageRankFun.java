package lucenestuff;

import java.util.ArrayList;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.alg.scoring.PageRank;

import edu.carleton.comp4601.graphstuff.MyGraph;
import edu.carleton.comp4601.resources.MyMongoDB;

public class PageRankFun {
	
	
	
	MyGraph graphObj;
	
	public PageRankFun(){
		graphObj = MyGraph.getInstance();
		
	}
	
	public Map<String, Double> produceScoreArray(){
		Graph<String, DefaultEdge> graph = graphObj.getGraph();
		PageRank<String, DefaultEdge> ranks = new PageRank<String, DefaultEdge>(graph);
			
		return ranks.getScores();
	}
	
	public void voidUpdateDatabase(Map<String, Double> scores){
		//MyMongoDB.getInstance().updateScores(scores);
	}

}
