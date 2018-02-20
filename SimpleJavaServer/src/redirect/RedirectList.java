package redirect;

import java.util.ArrayList;

public class RedirectList {
    private ArrayList<MovedPath> movedPaths = new ArrayList<MovedPath>();
    
    public RedirectList() {
        movedPaths.add(new MovedPath("/htm.htm", "/index.html"));
    }
    
    public boolean isMoved(String pathToCheck) {
        for(MovedPath path : movedPaths) {
            if(path.getCurrentPath().equals(pathToCheck)) {
                return true;
            }
        }
        return false;
    }
    
    public String getRedirectPath(String pathToCheck) {
        for(MovedPath path : movedPaths) {
            if(path.getCurrentPath().equals(pathToCheck)) {
                return path.getRedirectPath();
            }
        }
        return "No redirect path";
    }
    
    private class MovedPath {
        String currentPath;
        String redirectPath;
        
        MovedPath(String currentPath, String redirectPath) {
            this.currentPath = currentPath;
            this.redirectPath = redirectPath;
        }
        
        private String getCurrentPath() {
            return this.currentPath;
        }
        
        private String getRedirectPath() {
            return this.redirectPath;
        }
    }
}