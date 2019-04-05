

import java.util.ArrayList;
import java.util.Stack;


public class AVLTree implements AVLTreeADT{

    private int size;

    private AVLTreeNode root;
// the default constructor 
	//Insialise the tree with size 0 and create a root with the default values
	public AVLTree()
	{
		size = 0;
		root = new AVLTreeNode();
	}
	// set new root
    @Override
	public void setRoot(AVLTreeNode node)
	{
		root = node;
	}
	// get the root of the tree
    @Override
	public AVLTreeNode root()
	{
		return root;
	}
	// check if this node is the root of the tree
    @Override
	public boolean isRoot(AVLTreeNode node)
	{
		if(node.getKey() == root.getKey())
			return true;
		else
			return false;
	}
	// get the size of the tree
    @Override
	public int getSize()
	{
		return size;
	}
	//get the node with specific key
    @Override
	public AVLTreeNode get(AVLTreeNode node, int key)
	{
		// no node in the tree
		if(node.getHeight() == 0)
			return null;
		//loop until you find the node with this specific key 
		while (node != null)
		{
			//we found node 
			if(node.getKey() == key)
				return node;
			// the current node's key is greater than the key
			else if(node.getKey() > key)
				node = node.getLeft(); // try with the left node
			// the current node's key is smaller than the key
			else if(node.getKey() < key)
				node = node.getRight(); // try with right node
		}

		return null; // not found
	}
	// get the node with the smallest the key
    @Override
	public AVLTreeNode smallest(AVLTreeNode node)
	{
		if(node == null)// root with null
			return null;
		// loop until you find the most left node in the tree
		while (!node.isLeaf())
		{
			node = node.getLeft();
		}

		return node ;
	}
	// put new node in the tree and return the object of it
    @Override
	public AVLTreeNode put(AVLTreeNode node, int key, int data) throws TreeException
	{
		AVLTreeNode newRecord = new AVLTreeNode(key, data);
		// if the tree is empty put the new record as a root
		if(node.getHeight() == 0)
		{
			root = newRecord;
			root.setHeight(1);// set the height with 1
		}
		else
		{   //search for correct place to put the new record in it
			while (!node.isLeaf() || node.getHeight()==1)
			{
				// duplicate key found
				if(node.getKey() == key)
					throw new TreeException("a duplicate key is attempted to be inserted into the tree");
				// if the key less than that current node
				else if(node.getKey() > key) 
				if(node.getLeft()==null) // found the correct place for the new record
				{
					node.setLeft(newRecord); // put the new record in the left of the current node
					newRecord.setParent(node); // set it's parent
					newRecord.setHeight(1); // set it's height
					break; // break the loop and return
				}
				else
					node = node.getLeft(); // search again for the correct place in the left child
				// the same but now in the right child or right side
				else if(node.getKey() < key)
					if(node.getRight()==null)
					{
						node.setRight(newRecord);
						newRecord.setParent(node);
						newRecord.setHeight(1);
						break;
					}
					else
						node = node.getRight();
			}
			// recompute the height of the tree from the parent of the node to the root
			while(!node.isRoot())
			{
				recomputeHeight(node);
				node = node.getParent();
			}
			recomputeHeight(root);
		}
		size++; // increase the size by one
		return newRecord;
	}
	// remove the node with specific key and return the parent of it or the new node that is put instead of the removed node
    @Override
	public AVLTreeNode remove(AVLTreeNode node, int key) throws TreeException
	{
		// check if the key is found
		node = get(node,key);
		if(node == null)
			throw new TreeException("there is no node that has the key : " + key);
		boolean rightLeftFlag = true;// for checking which child (right or left)
		AVLTreeNode parent = node.getParent();
		// check if this node is the right child 
		if(parent != null)
			if(parent.getRight().getKey() == node.getKey())
				rightLeftFlag = false;
		// if the node has no children and we can remove it without any problem
		if(node.isLeaf())
		{
			// remove the node from it's parent
			if(rightLeftFlag)
				parent.setLeft(null);
			else
				parent.setRight(null);
			node = parent; // the node that will be returned
			// recompute heights 
		    while(node != null && !node.isRoot())
		    {
				recomputeHeight(node);
				node = node.getParent();
		    }
		    recomputeHeight(root);
		}
		// if the node has a left childonly only
		else if(node.getLeft() != null && node.getRight() == null)
		{
			// make the left child as the node that is placed instead of the removed one
			node.getLeft().setParent(parent);
			if(parent != null)
			{
				if(rightLeftFlag)
					parent.setLeft(node.getLeft());
				else
					parent.setRight(node.getLeft());
			}
			else
			{
				// if the removed node is the root, rest the root
				parent = node.getLeft();
				root = parent;
			}
			node = parent;
			while(parent != null && !parent.isRoot())
			{
				recomputeHeight(node);
				node = node.getParent();
			}
			recomputeHeight(root);
		}
		// if the node has a right child only
		else if(node.getLeft() == null && node.getRight() != null)
		{
			node.getRight().setParent(parent);
			if(parent != null)
			{
				if(rightLeftFlag)
					parent.setLeft(node.getRight());
				else
					parent.setRight(node.getRight());
			}
			else
			{
				parent = node.getRight();
				root = parent;
			}
			node = parent;
			while(parent != null && !parent.isRoot())
			{
				recomputeHeight(node);
				node = node.getParent();
			}
			recomputeHeight(root);
		}
		// if the node has two children
		else if(node.getLeft() != null && node.getRight() != null)
		{
			// the new node will be the node that has the smallest key in the sub tree right subtree(the right child is the root of that subtree)
			AVLTreeNode newNode = smallest(node.getRight());
			AVLTreeNode oldParent = newNode.getParent(); // the parent of the new nodenode
			// remove the new node from it's old position in the tree
			if(oldParent.getLeft().getKey() == newNode.getKey())
				oldParent.setLeft(null);
			else if(oldParent.getRight().getKey() == newNode.getKey())
				oldParent.setRight(null);
			// add the new node in it's new position
			newNode.setLeft(node.getLeft());// set the left child of the removed node to be the left of the new node
			// if the right child of the removed node is not the new node add it as the right child of the new one
			if(node.getRight() != newNode)
			    newNode.setRight(node.getRight());
			// set the new node as the parent of removed node children
			if(newNode.getLeft() != null)
				newNode.getLeft().setParent(newNode);
			if(newNode.getRight() != null)
				newNode.getRight().setParent(newNode);
			//set the parent of the new node 
			if(parent != null)
			{
				if(rightLeftFlag)
					parent.setLeft(newNode);
				else
					parent.setRight(newNode);
				newNode.setParent(parent);
			}
			// if the removed node is the root
			else
			{
				parent = newNode;
				root = parent;
			}
			parent = newNode;
			node = newNode;
			while(!node.isRoot())
			{
				recomputeHeight(node);
				node = node.getParent();
			}
			recomputeHeight(root);
		}  
		size--;// decrease the size by one
		return parent;
	}
	// get the all nodes in the tree in increasing order
    @Override
	public ArrayList<AVLTreeNode> inorder(AVLTreeNode node)
	{
		ArrayList<AVLTreeNode> list = new ArrayList<AVLTreeNode>();
		inorderRec(node,list);
		return list;
	}

    @Override
	public void inorderRec(AVLTreeNode node, ArrayList<AVLTreeNode> list)
	{
		if (node == null)// return null if the tree is empty
			return;
		Stack<AVLTreeNode> stack = new Stack<AVLTreeNode>();
		// push right in stack then the parent then the left
		if(node.getRight() != null)
			stack.push(node.getRight());
		stack.push(node);
		if(node.getLeft() != null)
			stack.push(node.getLeft());
		while(!stack.empty())
		{
			node = stack.pop();// get the top of the stack
			// if this is a leaf node
			if(node.getLeft() == null && node.getRight() == null)
			{
				list.add(node);// put this node in the list
				if(!stack.empty())
					node = stack.pop();
				// loop until the stack be empty
				while(!stack.empty())
				{
					// if the this node is the parent node and the top of stack is it's right node put it in the list
					if(node.getRight() == stack.lastElement())
						list.add(node);
					else
					{
						if(node.getLeft() == null && node.getRight() == null)
							list.add(node);
						else
						{
							// if this is a right child and has a children then expand it
							// push it's right child then push it then push it's left child
							if(node.getRight() != null)
								stack.push(node.getRight());
							stack.push(node);
							if(node.getLeft() != null)
								stack.push(node.getLeft());
						}
					}
					// pop the last node in the stack
					if(stack.size() != 1)
						node = stack.pop();
					else // the stack is empty
						break;
				}
			}
			else
			{   // expand the current node
				if(node.getRight() != null)
					stack.push(node.getRight());
				stack.push(node);
				if(node.getLeft() != null)
					stack.push(node.getLeft());
				else
					list.add(stack.pop());
			}
		}
	}

    @Override
	public void recomputeHeight(AVLTreeNode node)
	{
		if(node == null) // the tree is empty
			return;
		// set the height of the node by the max height of it's children +1
		if(node.getLeft() != null && node.getRight() != null)
			node.setHeight(Math.max(node.getLeft().getHeight(), node.getRight().getHeight())+1);
		else if(node.getLeft() != null)
			node.setHeight(node.getLeft().getHeight()+1);
		else if(node.getRight() != null)
			node.setHeight(node.getRight().getHeight()+1);
		else  // a leaf node
			node.setHeight(1);
	}
// balancing the tree
    @Override
	public void rebalanceAVL(AVLTreeNode r, AVLTreeNode v)
	{
		if(v.getLeft() != null)
		{
			// if the left child has height taller than right child
			// if the right child not exists and left child has a height of 2 or more
			if(v.getRight() == null && v.getLeft().getHeight() >= 2)
			{
				if(v.getLeft().getRight() == null)
					rotate(v);// LL rotation
				//LR rotation
				else if(v.getLeft().getLeft() == null)
					rotation(v,v.getLeft(),v.getLeft().getRight());   
				//LL rotation
				else if(v.getLeft().getLeft().getHeight() >= v.getLeft().getRight().getHeight())
					rotate(v);
				//LR rotation
				else if(v.getLeft().getLeft().getHeight() < v.getLeft().getRight().getHeight())
					rotation(v,v.getLeft(),v.getLeft().getRight());   
			}
			// if the right child exists and has a height less than the left child by 2 or more
			else if(v.getRight() != null && v.getLeft().getHeight() - v.getRight().getHeight() >= 2)
			{
			    // same as the above 
				if(v.getLeft().getRight() == null)
					rotate(v);
				else if(v.getLeft().getLeft() == null)
					rotation(v,v.getLeft(),v.getLeft().getRight());   
				else if(v.getLeft().getLeft().getHeight() >= v.getLeft().getRight().getHeight())
					rotate(v);
				else if(v.getLeft().getLeft().getHeight() < v.getLeft().getRight().getHeight())
					rotation(v,v.getLeft(),v.getLeft().getRight());   
			}
			// if the two children not null and the right child is taller than the left child by 2 or more
			else if (v.getRight() != null && v.getRight().getHeight() - v.getLeft().getHeight() == 2)
			{
			    //RR rotation
				if(v.getRight().getLeft() == null)
					rotate(v);
				//RL rotation
				else if(v.getRight().getRight() == null)
					rotation(v,v.getLeft(),v.getRight().getLeft());   
				//RR rotation
				else if(v.getRight().getRight().getHeight() >= v.getRight().getLeft().getHeight())
					rotate(v);
				// RL rotation
				else if(v.getRight().getRight().getHeight() < v.getRight().getLeft().getHeight())
					rotation(v,v.getRight(),v.getRight().getLeft());   
			}
		}
		else if(v.getRight() != null)
		{
			// if the right child has height taller than left child
			// if the left child not exists and right child has a height of 2 or more
			if(v.getLeft( ) == null && v.getRight().getHeight() == 2)
			{
				if(v.getRight().getLeft() == null)
					rotate(v); // RR rotation
				// RL rotation
				else if(v.getRight().getRight() == null)
					rotation(v,v.getRight(),v.getRight().getLeft());   
				// RR rotation
				else if(v.getRight().getRight().getHeight() >= v.getRight().getLeft().getHeight())
					rotate(v);
				// RL rotation
				else if(v.getRight().getRight().getHeight() < v.getRight().getLeft().getHeight())
					rotation(v,v.getRight(),v.getRight().getLeft());    
			}
			// if the left child exists and has a height less than the right child by 2 or more
			else if(v.getLeft( ) != null && v.getLeft().getHeight() - v.getRight().getHeight() == 2)
			{
				if(v.getLeft().getRight() == null)
					rotate(v);//RR rotation
				// RL rotation
				else if(v.getLeft().getLeft() == null)
					rotation(v,v.getLeft(),v.getLeft().getRight());   
				// RR rotation
				else if(v.getLeft().getLeft().getHeight() >= v.getLeft().getRight().getHeight())
					rotate(v);
				//RL rotation
				else if(v.getLeft().getLeft().getHeight() < v.getLeft().getRight().getHeight())
					rotation(v,v.getLeft(),v.getLeft().getRight());   
			}
			// if the two children not null and the left child is taller than the right child by 2 or more
			else if (v.getLeft( ) != null && v.getRight().getHeight() - v.getLeft().getHeight() == 2)
			{
				if(v.getRight().getLeft() == null)
					rotate(v);//LL rotation
				// LR rotation
				else if(v.getRight().getRight() == null)
					rotation(v,v.getLeft(),v.getRight().getLeft());   
				//LL rotation
				else if(v.getRight().getRight().getHeight() >= v.getRight().getLeft().getHeight())
					rotate(v);
				// LR rotation
				else if(v.getRight().getRight().getHeight() < v.getRight().getLeft().getHeight())
					rotation(v,v.getRight(),v.getRight().getLeft());   
			}
		}
	}

    @Override
	public void putAVL(AVLTreeNode node, int key, int data) throws TreeException
	{
		// put the new node in the tree then loop to check the balance of the tree from the parent of it to the root of the tree
		AVLTreeNode subTreeRoot = put(node,key,data).getParent();
		while(subTreeRoot != null)
		{
			rebalanceAVL(node,subTreeRoot);
			subTreeRoot = subTreeRoot.getParent();
		}
	}

    @Override
	public void removeAVL(AVLTreeNode node, int key) throws TreeException
	{
		// remove the node in the tree then loop to check the balance of the tree from the new node that is put instead of it to the root of the tree
		AVLTreeNode subTreeRoot = remove(node,key);
		while(subTreeRoot != null)
		{
			rebalanceAVL(node,subTreeRoot);
			subTreeRoot = subTreeRoot.getParent();
		}
	}


	public AVLTreeNode rotate(AVLTreeNode node)
	{
		AVLTreeNode newParent = null;
		if(node != null)
		{
			if(isLeft(node)) // if LL rotating
			{
				newParent = node.getLeft(); // make the left child the new root of this subtree
				if(newParent.getRight() != null)
				{// if the left child has a right child then put it as a left child for the old root of the subtree
					node.setLeft(newParent.getRight());
					node.getLeft().setParent(node);
				}
				else
					node.setLeft(null);//make the old root of the subtreemake has no left child(remove the new root)
				newParent.setParent(node.getParent()); // set parent of the new root
				if(newParent.getParent() == null)
					root = newParent;// if the new root is the root of the tree
				newParent.setRight(node);
				// put the new root/left child of subtree tree a child of the parent of the old one instead of the old one
				if(node.getParent() != null && node.getParent().getLeft() == node)
					node.getParent().setLeft(newParent);
				else if(node.getParent() != null && node.getParent().getRight() == node)
					node.getParent().setRight(newParent);
				node.setParent(newParent); //make the left child the parent of it's paren
			}
			else // RR rotation
			{
				// the same as the above but for the right child
				newParent = node.getRight();
				if(newParent.getLeft() != null)
				{
				// we make here the left child is right child of the old root
					node.setRight(newParent.getLeft());
					node.getRight().setParent(node);
				}
				else
					node.setRight(null);
				newParent.setParent(node.getParent());
				if(newParent.getParent() == null)
					root = newParent;
				newParent.setLeft(node);
				if(node.getParent() != null && node.getParent().getLeft() == node)
					node.getParent().setLeft(newParent);
				else if(node.getParent() != null && node.getParent().getRight() == node)
					node.getParent().setRight(newParent);
				node.setParent(newParent);
			}
			// recompute heights 
			while(!node.isRoot())
			{
				recomputeHeight(node);
				node = node.getParent();
			}
			recomputeHeight(root);
		}

		return newParent; // return the new root
	}

	public AVLTreeNode rotation(AVLTreeNode z, AVLTreeNode y, AVLTreeNode x)
	{
		AVLTreeNode newParent = rotate(y); // first LL/RR rotation
		newParent = rotate(z); // second LL/RR rotation
		return newParent; // return the new root
	}
// check if this is a LL rotation or not
	private boolean isLeft(AVLTreeNode node)
	{
		if(node.getLeft() == null)
			return false;
		else if(node.getRight() == null)
			return true;
		else if(node.getLeft().getHeight() - node.getRight().getHeight() >= 2)
			return true;
		else if(node.getRight().getHeight() - node.getLeft().getHeight() >= 2)
			return false;

		return true;
	}

}