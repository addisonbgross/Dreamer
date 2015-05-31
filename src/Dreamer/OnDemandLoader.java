package Dreamer;

import Dreamer.enums.FileType;

public class OnDemandLoader implements Runnable {
	
	private static java.util.ArrayDeque<Resource> resourceList = new java.util.ArrayDeque<>(); 
	private static OnDemandLoader loader = new OnDemandLoader();
	
    public void run() {
        
    	while(true) {
        
    		try {
        		
    			if(resourceList.isEmpty())
        			Thread.sleep(10);
        		else
        			resourceList.pop().getResource();
			} catch (InterruptedException e) {
			
				System.out.println("Huh?");
			}
        }
    }
    
    static void queueResources(Resource r) { resourceList.push(r); }

    public static void Start() { (new Thread(loader)).start(); }
}

class Resource {

	String name;
	Object resource;
	FileType type;
	
	Resource(String s, FileType ft) {
	
		name = s;
		type = ft;
	}
	
	void getResource() {
	
		resource = Library.get(name, type);
		new ShadowedMessage(name + resource.toString(), 0, -200).add();
	}
}