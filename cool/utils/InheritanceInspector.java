package cool.utils;

import cool.structures.FunctionSymbol;
import cool.structures.IdSymbol;
import cool.structures.Symbol;
import cool.structures.SymbolTable;

import java.util.*;

public class InheritanceInspector {
    AbstractMap<ClassParserCtx, ClassParserCtx> classHierarchy;
    ArrayList<ClassParserCtx> visited;

    int maxTag = 0;
    public InheritanceInspector(AbstractMap<ClassParserCtx, ClassParserCtx> classHierarchy) {
        this.classHierarchy = classHierarchy;
        visited = new ArrayList<>();
    }

    public  ArrayList<ClassParserCtx> detectCycle(ClassParserCtx startingClass) {
        while(true) {
            if(visited.contains(startingClass))
                return visited;

            visited.add(startingClass);
            if(classHierarchy.containsKey(startingClass))
                startingClass = classHierarchy.get(startingClass);
            else return null;
            // the parent does'nt exist
            if(startingClass == null)
                return null;

            if(startingClass.getClassName().equals("Object"))
                return null;
        }
    }

    public boolean isSubtype(ClassParserCtx ancestorClass, ClassParserCtx childClass) {
        if(ancestorClass.getClassName().equals(childClass.getClassName()))
            return true;
        if(childClass.getClassName().equals("Object"))
            return false;
        while(classHierarchy.containsKey(childClass)) {
            childClass = classHierarchy.get(childClass);
            if (childClass.getClassName().equals(ancestorClass.getClassName()))
                return true;

            if (childClass.getClassName().equals("Object"))
                return false;
        }

        return false;
    }

    public ArrayList<ClassParserCtx> getInherintacePath(ClassParserCtx startingClass) {
        ArrayList<ClassParserCtx> path = new ArrayList<>();
        path.add(startingClass);
        while(classHierarchy.containsKey(startingClass)) {
            startingClass = classHierarchy.get(startingClass);
            path.add(startingClass);

            if(startingClass.getClassName().equals("Object"))
                break;
        }

        Collections.reverse(path);
        return path;
    }

    public String getCommonAncestor(ArrayList<ClassParserCtx> classes)  {
        ArrayList<ArrayList<ClassParserCtx>> paths = new ArrayList<>();
        int size = Integer.MAX_VALUE;
        for(var startingClass : classes) {
            var currentPath = getInherintacePath(startingClass);
            if(currentPath.size() < size)
                size = currentPath.size();

            paths.add(currentPath);
        }

        int index = -1;
        int j = 0;
        for(j = 0; j < size; j++) {
            String className = paths.get(0).get(j).getClassName();
            for(int i = 0; i < paths.size(); i++) {
                if(!paths.get(i).get(j).getClassName().equals(className)) {
                    index = j - 1;
                    break;
                }
            }

            if(index != -1)
                break;
        }
        if(j == size)
            return paths.get(0).get(j-1).getClassName();

        if(index == -1)
            return "Object";

        return paths.get(0).get(index).getClassName();
    }

    public FunctionSymbol searchMethod(ClassParserCtx startingClass, String methodName) {
        Symbol searchedMethod = null;
        while(classHierarchy.containsKey(startingClass)) {
            if((searchedMethod = startingClass.classSymbol.lookupMethod(methodName)) != null) {
                return (FunctionSymbol) searchedMethod;
            }
            startingClass = classHierarchy.get(startingClass);
            if(startingClass == null)
                break;
        }

        return null;
    }

    public IdSymbol searchAttribute(ClassParserCtx startingClass, String attributeName) {
        Symbol searchedId = null;
        while(classHierarchy.containsKey(startingClass)) {
            if((searchedId = startingClass.classSymbol.lookupId(attributeName)) != null) {
                return (IdSymbol) searchedId;
            }
            startingClass = classHierarchy.get(startingClass);
            if(startingClass == null)
                break;
        }

        return null;
    }

    public HashMap<ClassParserCtx, ArrayList<ClassParserCtx> > createTree() {
        HashMap<ClassParserCtx, ArrayList<ClassParserCtx> > classTree = new LinkedHashMap<>();
        for(var entry : classHierarchy.entrySet()) {
            var parent = entry.getValue();
            if(parent == null)
                continue;

            if(classTree.containsKey(entry.getValue())) {
                var children = classTree.get(parent);
                children.add(entry.getKey());
            } else {
                var children = new ArrayList<ClassParserCtx>();
                children.add(entry.getKey());
                classTree.put(parent, children);
            }
        }

        return classTree;
    }

    void dfs(ClassParserCtx currentClass, HashMap<ClassParserCtx, ArrayList<ClassParserCtx> > classTree,
             HashMap<String, GenericPair<Integer, Integer>> tags) {
        if(SymbolTable.basicTypes.contains(currentClass.getClassName()))
            return;

        currentClass.tag = maxTag;
        currentClass.endingTag = maxTag;
        maxTag++;

        tags.put(currentClass.getClassName(),
                new GenericPair<Integer, Integer>(currentClass.tag, currentClass.endingTag));
        var children = classTree.get(currentClass);
        if(children == null)
            return;

        int currentMax = 0;
        for(var child : children) {
            dfs(child, classTree, tags);

            if(child.endingTag > currentClass.endingTag) {
                currentClass.endingTag = child.endingTag;
                tags.get(currentClass.getClassName()).setValue(child.endingTag);
            }
            if(child.endingTag > currentMax)
                currentMax = child.endingTag;
        }
        tags.get(currentClass.getClassName()).setValue(currentMax);
    }

    public HashMap<String, GenericPair<Integer, Integer>> addTags(ClassParserCtx startingClass, int tag) {
        var classTree = createTree();
        HashMap<String, GenericPair<Integer, Integer>> tags = new LinkedHashMap<>();
        dfs(startingClass, classTree, tags);

        tags.put("Int", new GenericPair<Integer, Integer>(maxTag, maxTag));
        maxTag++;

        tags.put("String", new GenericPair<Integer, Integer>(maxTag, maxTag));
        maxTag++;

        tags.put("Bool", new GenericPair<Integer, Integer>(maxTag, maxTag));
        tags.get("Object").setValue(maxTag);

        return tags;
    }
}
