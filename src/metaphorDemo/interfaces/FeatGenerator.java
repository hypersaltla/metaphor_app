package metaphorDemo.interfaces;

public interface FeatGenerator {
	//public void setResourcePath(String path); // set up the path to the external resource for generating data
	public void setParams(Object paramList);
	public void loadResource(); // load these resource if given the path
	public Object generateFeat(Object dataList); // get 1 or more data and generate corresponding features
}