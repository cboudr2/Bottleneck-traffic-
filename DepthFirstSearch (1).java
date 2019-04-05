
import java.util.Iterator;
import java.util.Stack;

public class DepthFirstSearch {
    
    private RouteGraph inputGraph;
    
    private Stack<Intersection> stack;
    
    public DepthFirstSearch(RouteGraph graph)
    {
		// create new object with a new RouteGraph 
        inputGraph = graph;
		// stack for storing the visited nodes
        stack = new Stack<Intersection>();
    }

    public Stack<Intersection> path(Intersection startVertex, Intersection endVertex) throws GraphException{
        
        stack.clear(); // clear the stack
        pathRec(startVertex,endVertex);// call the pathRec to get path in the stack
        
        return stack;
    }
    
    public void pathRec(Intersection startVertex, Intersection endVertex)
    {
        try {
		    // check if the start vertex visited or not
            if(!startVertex.getMark())
            {
                stack.push(startVertex); // push the vertex in the stack
                startVertex.setMark(true); // make this vertex visited
            }
			// get all incident roads for this vertex
            Iterator iterator = inputGraph.incidentRoads(startVertex);
            while (iterator.hasNext())
            {   // loop until you find the path
                Road CurrentRoad = (Road)iterator.next(); //get the next road n
                Intersection theSecondIntersection = null;
				// get the another endpoint intersection of the road
                if(CurrentRoad.getFirstEndpoint() == startVertex)
                    theSecondIntersection = CurrentRoad.getSecondEndpoint();
                else
                    theSecondIntersection = CurrentRoad.getFirstEndpoint();
		        // if this intersectionbeforenot has not visited before
                if(!theSecondIntersection.getMark())
                {   // push the second endpoint intersection in the stack
                    stack.push(theSecondIntersection);
                    theSecondIntersection.setMark(true); // set it visited
					// found the correct path 
                    if(theSecondIntersection == endVertex)
                        return;// end the search
                    else // call the function itself recursivlly
                        pathRec(theSecondIntersection,endVertex);
					// after the recursive call check if the last vertex in the stack is the endpoint of the path and the  path is found
                    if(stack.peek() == endVertex)
                        return; // stop searching
                    else
                    {
					     // pop the last vertex from the stack and set it not visited and search again for another vertex in another path
                         stack.pop();
                         theSecondIntersection.setMark(false);
                    }
                }
            }
        } catch (GraphException e) {
            System.out.println("intersection does not exist");
        }
    }
    
}
