package br.dcc.ufba.themoviefinder.lodweb;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node implements Comparator<Node>, Comparable<Node> {

	private String id;

	private String URI;

	private String label;

	private String content;
	
	private int userId;
	
	private double rankingScore;
	
	public Map<String, Double> relationalFeatures;

	public Map<String, String> observedAtrributes;

	public Map<String, String> observedAtrributesOriginal;	
	
	public List<Node> neighbours = new ArrayList<Node>();	
	
	public Node() {}
	
	private List<Node> nodes = new ArrayList<Node>();

	private String domain;	

	public double getRankingScore() {
		return rankingScore;
	}

	public void setRankingScore(double rankingScore) {
		this.rankingScore = rankingScore;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public Node(String label, Node node) {
		super();
		this.label = label;
		this.addNode(node);
	}

	public Node(String label, String uri) {
		this.label = label;
		this.URI = uri;
	}

	public Node(String id, String label, String uri) {
		this.id = id;
		this.label = label;
		this.URI = uri;
	}
	
	public Node(String id, String label, String uri, int userId) {
		this.id = id;
		this.label = label;
		this.URI = uri;
		this.userId = userId;
	}	

	public Node(String id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @param testKey
	 * @return
	 */
	public double getTopValueByKey(String testKey) {
		double top = 0d;
		for (String key : relationalFeatures.keySet()) {
			if (key.equals(testKey)) {
				double toptest = relationalFeatures.get(key);
				if (toptest > top) {
					top = toptest;
				}
			}
		}
		return top;
	}

	public Map<String, Double> getRelationalFeatures() {
		if (relationalFeatures == null) {
			relationalFeatures = new HashMap<String, Double>();
		}
		return relationalFeatures;
	}
	public void setRelationalFeatures(Map<String, Double> relationalFeatures) {
		this.relationalFeatures = relationalFeatures;
	}

	public Map<String, String> getObservedAtrributesOriginal() {
		return observedAtrributesOriginal;
	}

	public void setObservedAtrributesOriginal(Map<String, String> observedAtrributesOriginal) {
		this.observedAtrributesOriginal = observedAtrributesOriginal;
	}

	public Map<String, String> getObservedAtrributes() {
		return observedAtrributes;
	}

	public void setObservedAtrributes(Map<String, String> observedAtrributes) {
		this.observedAtrributes = observedAtrributes;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String uri) {
		this.URI = uri;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void addNode(Node node) {
		if (this.getNodes() == null) {
			this.nodes = new ArrayList<Node>();
		}
		
		if (!this.getNodes().contains(node)) {
			nodes.add(node);	
		}
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public List<Node> getLinkedNodes(Node node) {
		return node.getNodes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.URI +"   ID:"+ this.id;
	}

	public boolean isUnlabeled() {
		return this.getLabel() == null;
	}

	@Override
	public int compareTo(Node o) {
		if (Integer.valueOf(this.id) < Integer.valueOf(o.id)) {
			return -1;
		} else if (Integer.valueOf(this.id) > Integer.valueOf(o.id)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public int compare(Node o1, Node o2) {
		if (o1.rankingScore < o2.rankingScore) {
			return 1;
		} else if (o1.rankingScore > o2.rankingScore) {
			return -1;
		} else {
			return 0;
		}
	}	

}
