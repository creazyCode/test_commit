package com.imall.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Range;

import com.imall.common.utils.ObjectUtils;

/**
 * this class is used to wrap the pager data
 * 
 * @author jianxunji
 * 
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(include=com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL)
public class Pager<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9221210654451940264L;
	private int total; //the total pages
	@Range(min=1, max=200)
	private int pageSize; //the page size
	@Range(min=1)
	private int page; //the current page index
	private int records; //total rows of data
	private int startRow; //the start row of the page
	private int endRow; // the end row of the page
	
	private List<T> rows;
	
	private Boolean shouldRandom = Boolean.FALSE;

	/**
	 * @param records total rows of data
	 * @param pageSize the page size
	 * @param page the current page index
	 * @param rows the data of the page
	 */
	public Pager(int records, int pageSize, int page, List<T> rows) {
		this.records = records;
		this.pageSize = pageSize;
		this.page = page;
		this.rows = rows;
		
		this.total = this.records / this.pageSize;
		int mod = this.records % this.pageSize;
		if (mod > 0) {
			this.total ++;
		}
		this.startRow = getStartRow(this.pageSize, this.page);
		this.endRow = this.startRow + this.pageSize;
	}
	
	/**
	 * @param pageSize
	 * @param page
	 * @return
	 */
	public static int getStartRow(int pageSize, int page){
		return (page - 1) * pageSize;
	}
	
	public int getStartRow() {
		return this.startRow;
	}

	public int getRecords() {
		return this.records;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public void setRecords(int records) {
		this.records = records;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotal() {
		return this.total;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public void first() {
		this.page = 1;
		this.startRow = 0;
	}

	public void previous() {
		if (this.page == 1 || this.page == 0) {
			return;
		}
		this.page --;
		this.startRow = (this.page - 1) * this.pageSize;
	}

	public void next() {
		if (this.page < this.records) {
			this.page ++;
		}
		this.startRow = (this.page - 1) * this.pageSize;
	}

	public void last() {
		this.page = this.records;
		this.startRow = (this.page - 1) * this.pageSize;
	}

	public int getEndRow() {
		return this.endRow;
	}

	public List<T> getRows() {
		return this.rows;
	}

	public void setRows(List<T> data) {
		this.rows = data;
	}
	
	public void initRowsFromAllRows(List<T> allRows){
		if(allRows != null){
			if(allRows.size() < this.getStartRow()){
				this.setRows(new ArrayList<T>());
			}else{
				List<T> rows = allRows.subList(this.getStartRow() - 1, allRows.size() < this.getEndRow() ? allRows.size() : this.getEndRow());
				this.setRows(rows);
			}
		}
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + this.total;
		result = 37 * result + this.pageSize;
		result = 37 * result + this.page;
		if(this.rows != null){
			result = 37 * result + this.rows.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Pager)){
			return false;
		}
		if(this == obj){
			return true;
		}
		Pager<T> pager = (Pager<T>) obj;
		if(this.total != pager.total
				|| this.pageSize != pager.pageSize
				|| this.page != pager.page){
			return false;
		}
		return ObjectUtils.isTwoCollectionDeeplyEquals(this.rows, pager.rows, null, true);
	}

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	/**
	 * 是否有下一页
	 * 
	 * @return
	 */
	public Boolean getHasNextPage(){
		if(this.total > 0 && this.page < this.total){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public Boolean getShouldRandom() {
		return shouldRandom;
	}

	public void setShouldRandom(Boolean shouldRandom) {
		this.shouldRandom = shouldRandom;
	}
}
