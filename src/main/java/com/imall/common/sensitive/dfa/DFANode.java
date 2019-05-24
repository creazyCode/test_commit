package com.imall.common.sensitive.dfa;

import java.util.LinkedList;
import java.util.List;

public class DFANode {

	private int value;

	private List<DFANode> subNodes;

	private boolean isLast;

	public DFANode(int value) {
		this.value = value;
	}

	public DFANode(int value, boolean isLast) {
		this.value = value;
		this.isLast = isLast;
	}


	private DFANode addSubNode(final DFANode subNode) {
		if (subNodes == null)
			subNodes = new LinkedList<DFANode>();
		subNodes.add(subNode);
		return subNode;
	}

	public DFANode addIfNoExist(final int value, final boolean isLast) {
		if (subNodes == null) {
			return addSubNode(new DFANode(value, isLast));
		}
		for (DFANode subNode : subNodes) {
			if (subNode.value == value) {
				if (!subNode.isLast && isLast)
					subNode.isLast = true;
				return subNode;
			}
		}
		return addSubNode(new DFANode(value, isLast));
	}

	public DFANode querySub(final int value) {
		if (subNodes == null) {
			return null;
		}
		for (DFANode subNode : subNodes) {
			if (subNode.value == value)
				return subNode;
		}
		return null;
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	@Override
	public int hashCode() {
		return value;
	}

}