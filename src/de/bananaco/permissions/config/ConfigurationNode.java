package de.bananaco.permissions.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represents a configuration node.
 */
public class ConfigurationNode extends CommentNode {
    protected Map<String, Object> root;

    protected ConfigurationNode(Map<String, Object> root) {
    	super(new HashMap<String, String>());
        this.root = root;
    }

    /**
     * Gets all of the cofiguration values within the Node as
     * a key value pair, with the key being the full path and the
     * value being the Object that is at the path.
     *
     * @return A map of key value pairs with the path as the key and the object as the value
     */
    public Map<String, Object> getAll() {
        return recursiveBuilder(root);
    }

    /**
     * A helper method for the getAll method that deals with the recursion
     * involved in traversing the tree
     *
     * @param node The map for that node of the tree
     * @return The fully pathed map for that point in the tree, with the path as the key
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> recursiveBuilder(Map<String, Object> node) {
        Map<String, Object> map = new TreeMap<String, Object>();

        Set<String> keys = node.keySet();
        for( String k : keys ) {
            Object tmp = node.get(k);
            if( tmp instanceof Map<?,?> ) {
                Map<String, Object> rec = recursiveBuilder((Map <String,Object>) tmp);

                Set<String> subkeys = rec.keySet();
                for( String sk : subkeys ) {
                    map.put(k + "." + sk, rec.get(sk));
                }
            }
            else {
                map.put(k, tmp);
            }
        }

        return map;
    }

    /**
     * Gets a property at a location. This will either return an Object
     * or null, with null meaning that no configuration value exists at
     * that location. This could potentially return a default value (not yet
     * implemented) as defined by a plugin, if this is a plugin-tied
     * configuration.
     *
     * @param path path to node (dot notation)
     * @return object or null
     */
    @SuppressWarnings("unchecked")
    public Object getProperty(String path) {
        if (!path.contains(".")) {
            Object val = root.get(path);

            if (val == null) {
                return null;
            }
            return val;
        }

        String[] parts = path.split("\\.");
        Map<String, Object> node = root;

        for (int i = 0; i < parts.length; i++) {
            Object o = node.get(parts[i]);

            if (o == null) {
                return null;
            }

            if (i == parts.length - 1) {
                return o;
            }

            try {
                node = (Map<String, Object>) o;
            } catch (ClassCastException e) {
                return null;
            }
        }

        return null;
    }

    /**
     * Set the property at a location. This will override existing
     * configuration data to have it conform to key/value mappings.
     *
     * @param path
     * @param value
     */
    public void setProperty(String path, String value) {
    	setProperty(path,Arrays.asList(value));
    }
    @SuppressWarnings("unchecked")
    public void setProperty(String path, List<String> value) {
        if (!path.contains(".")) {
            root.put(path, value);
            return;
        }

        String[] parts = path.split("\\.");
        Map<String, Object> node = root;

        for (int i = 0; i < parts.length; i++) {
            Object o = node.get(parts[i]);

            // Found our target!
            if (i == parts.length - 1) {
                node.put(parts[i], value);
                return;
            }

            if (o == null || !(o instanceof Map)) {
                // This will override existing configuration data!
                o = new HashMap<String, Object>();
                node.put(parts[i], o);
            }

            node = (Map<String, Object>) o;
        }
    }

    /**
     * Get a list of keys at a location. If the map at the particular location
     * does not exist or it is not a map, null will be returned.
     *
     * @param path path to node (dot notation)
     * @return list of keys
     */
    @SuppressWarnings("unchecked")
    public List<String> getKeys(String path) {
        if (path == null) {
            return new ArrayList<String>(root.keySet());
        }
        Object o = getProperty(path);
        if (o == null) {
            return null;
        } else if (o instanceof Map) {
            return new ArrayList<String>(((Map<String, Object>) o).keySet());
        } else {
            return null;
        }
    }

    /**
     * Returns a list of all keys at the root path
     *
     * @return List of keys
     */
    public List<String> getKeys() {
        return new ArrayList<String>(root.keySet());
    }

    /**
     * Gets a list of objects at a location. If the list is not defined,
     * null will be returned. The node must be an actual list.
     *
     * @param path path to node (dot notation)
     * @return boolean or default
     */
    @SuppressWarnings("unchecked")
    public List<Object> getList(String path) {
        Object o = getProperty(path);

        if (o == null) {
            return null;
        } else if (o instanceof List) {
            return (List<Object>) o;
        } else {
            return null;
        }
    }

    /**
     * Gets a list of strings. Non-valid entries will not be in the list.
     * There will be no null slots. If the list is not defined, the
     * default will be returned. 'null' can be passed for the default
     * and an empty list will be returned instead. If an item in the list
     * is not a string, it will be converted to a string. The node must be
     * an actual list and not just a string.
     *
     * @param path path to node (dot notation)
     * @param def default value or null for an empty list as default
     * @return list of strings
     */
    public List<String> getStringList(String path) {
    	return getStringList(path, null);
    }
    public List<String> getStringList(String path, List<String> def) {
        List<Object> raw = getList(path);

        if (raw == null) {
            return def != null ? def : new ArrayList<String>();
        }

        List<String> list = new ArrayList<String>();

        for (Object o : raw) {
            if (o == null) {
                continue;
            }

            list.add(o.toString());
        }

        return list;
    }

    /**
     * Gets a list of nodes. Non-valid entries will not be in the list.
     * There will be no null slots. If the list is not defined, the
     * default will be returned. 'null' can be passed for the default
     * and an empty list will be returned instead. The node must be
     * an actual node and cannot be just a boolean,
     *
     * @param path path to node (dot notation)
     * @param def default value or null for an empty list as default
     * @return list of integers
     */
    @SuppressWarnings("unchecked")
    public List<ConfigurationNode> getNodeList(String path, List<ConfigurationNode> def) {
        List<Object> raw = getList(path);

        if (raw == null) {
            return def != null ? def : new ArrayList<ConfigurationNode>();
        }

        List<ConfigurationNode> list = new ArrayList<ConfigurationNode>();

        for (Object o : raw) {
            if (o instanceof Map) {
                list.add(new ConfigurationNode((Map<String, Object>) o));
            }
        }

        return list;
    }

    /**
     * Get a configuration node at a path. If the node doesn't exist or the
     * path does not lead to a node, null will be returned. A node has
     * key/value mappings.
     *
     * @param path
     * @return node or null
     */
    @SuppressWarnings("unchecked")
    public ConfigurationNode getNode(String path) {
        Object raw = getProperty(path);

        if (raw instanceof Map) {
            return new ConfigurationNode((Map<String, Object>) raw);
        }

        return null;
    }

    /**
     * Get a list of nodes at a location. If the map at the particular location
     * does not exist or it is not a map, null will be returned.
     *
     * @param path path to node (dot notation)
     * @return map of nodes
     */
    @SuppressWarnings("unchecked")
    public Map<String, ConfigurationNode> getNodes(String path) {
        Object o = getProperty(path);

        if (o == null) {
            return null;
        } else if (o instanceof Map) {
            Map<String, ConfigurationNode> nodes = new HashMap<String, ConfigurationNode>();

            for (Map.Entry<String, Object> entry : ((Map<String, Object>) o).entrySet()) {
                if (entry.getValue() instanceof Map) {
                    nodes.put(entry.getKey(), new ConfigurationNode((Map<String, Object>) entry.getValue()));
                }
            }

            return nodes;
        } else {
            return null;
        }
    }

    /**
     * Remove the property at a location. This will override existing
     * configuration data to have it conform to key/value mappings.
     *
     * @param path
     */
    @SuppressWarnings("unchecked")
    public void removeProperty(String path) {
        if (!path.contains(".")) {
            root.remove(path);
            return;
        }

        String[] parts = path.split("\\.");
        Map<String, Object> node = root;

        for (int i = 0; i < parts.length; i++) {
            Object o = node.get(parts[i]);

            // Found our target!
            if (i == parts.length - 1) {
                node.remove(parts[i]);
                return;
            }

            node = (Map<String, Object>) o;
        }
    }
}