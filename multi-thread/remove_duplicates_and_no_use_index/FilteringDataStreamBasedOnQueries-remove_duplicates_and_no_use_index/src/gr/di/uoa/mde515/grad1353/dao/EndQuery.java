package gr.di.uoa.mde515.grad1353.dao;

public class EndQuery extends QueryType {

	public EndQuery(final String queryType, final int queryId) {
		super(queryType, queryId);
	}

	@Override
	public String toString() {
		return new StringBuilder().
				append(getQueryType()).
				append(" ").
				append(getQueryId()).toString();
	}
}
