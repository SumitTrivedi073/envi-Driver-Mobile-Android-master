package sgs.env.ecabsdriver.model;

public class ResponseConfig {
	
	
	private SearchConfig searchConfig;
	
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}
	
	public void setSearchConfig(SearchConfig searchConfig) {
		this.searchConfig = searchConfig;
	}
	
	@Override
	public String toString() {
		return "Response{" +
				"searchConfig='" + searchConfig +
				'}';
	}
}
