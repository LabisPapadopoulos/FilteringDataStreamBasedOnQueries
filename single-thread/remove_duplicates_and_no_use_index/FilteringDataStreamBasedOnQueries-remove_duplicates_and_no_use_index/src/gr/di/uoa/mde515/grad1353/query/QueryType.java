package gr.di.uoa.mde515.grad1353.query;

public abstract class QueryType {
	private String queryType;
	private int queryId;
	
	public QueryType(final String queryType, final int queryId) {
		this.queryType = queryType;
		this.queryId = queryId;
	}
	
	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public int getQueryId() {
		return queryId;
	}

	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}
}
