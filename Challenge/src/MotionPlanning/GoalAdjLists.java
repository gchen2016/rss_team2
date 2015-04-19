package MotionPlanning;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

public class GoalAdjLists {
	
	HashMap<Point2D.Double, HashMap<Point2D.Double, ArrayList<Point2D.Double>>> pathGrid;
	HashMap<Point2D.Double, HashMap<Point2D.Double, Double>> distanceGrid;
	Point2D.Double goal;
	
	public GoalAdjLists(Point2D.Double goalPoint)
		{
		goal = goalPoint;		
		}

	public void addBiPath(Point2D.Double from, Point2D.Double to, ArrayList<Point2D.Double> path, double dist)
		{
		double pathDistance = dist;//getDistance(path);
		if (distanceGrid.get(from)==null) distanceGrid.put(from, new HashMap<Point2D.Double,Double>());
		if (distanceGrid.get(to)==null) distanceGrid.put(to, new HashMap<Point2D.Double,Double>());
		distanceGrid.get(from).put(to, pathDistance);
		distanceGrid.get(to).put(from, pathDistance);
		
		if (pathGrid.get(from)==null) pathGrid.put(from, new HashMap<Point2D.Double,ArrayList<Point2D.Double>>());
		if (pathGrid.get(to)==null) pathGrid.put(to, new HashMap<Point2D.Double,ArrayList<Point2D.Double>>());
		
		ArrayList<Point2D.Double> reversePath = new ArrayList<Point2D.Double>();
		Collections.copy(reversePath, path);
		Collections.reverse(reversePath);
		
		pathGrid.get(from).put(to, path);
		pathGrid.get(to).put(from, reversePath);
		}
	
	public ArrayList<Point2D.Double> getClosestPathFrom(Point2D.Double from)
		{
		HashMap<Point2D.Double,Double> originDistMap = distanceGrid.get(from);
		
		Point2D.Double closestPoint = null;
		double closestDistance = Double.MAX_VALUE;
		
		for (Point2D.Double tos : originDistMap.keySet())
			{
			if (originDistMap.get(tos) < closestDistance)
				{
				closestDistance = originDistMap.get(tos);
				closestPoint = tos;
				}
			}
		return pathGrid.get(from).get(closestPoint);
		}
	
	public ArrayList<Point2D.Double> getUnvisited()
		{return (ArrayList<Point2D.Double>) distanceGrid.keySet();}

	
	public void useBiPath(Point2D.Double from, Point2D.Double to)
		{
		//distanceGrid.get(from).remove(to);
		distanceGrid.remove(from);
		distanceGrid.get(to).remove(from);
		//pathGrid.get(from).remove(to);
		pathGrid.remove(from);
		pathGrid.get(to).remove(from);
		}
	
	private double getDistance(ArrayList<Point2D.Double> path)
		{
		double dist = 0;
		for (int i=0; i<path.size()-1; i++)
			{dist += Math.sqrt(Math.pow(path.get(i).x-path.get(i+1).x, 2) + Math.pow(path.get(i).y-path.get(i+1).y, 2));}
		return dist;
		}
	
}
