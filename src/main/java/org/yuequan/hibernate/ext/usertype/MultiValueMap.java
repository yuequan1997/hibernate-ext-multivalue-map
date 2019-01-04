package org.yuequan.hibernate.ext.usertype;

import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.usertype.UserCollectionType;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.usertype.UserCollectionType;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * 多值Map扩展Hibernate
 * @see {@link  org.hibernate.usertype.UserCollectionType}
 * @author yuequan
 * @since 1.0
 */
public class MultiValueMap<K, V> implements UserCollectionType {

    @Override
    public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister) throws HibernateException {
        return new PersistentMultiValueMap(session);
    }

    @Override
    public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
        return new PersistentMultiValueMap(session, (org.springframework.util.MultiValueMap) collection);
    }

    @Override
    public Iterator getElementsIterator(Object collection) {
        return ((org.springframework.util.MultiValueMap) collection).values().iterator();
    }

    @Override
    public boolean contains(Object collection, Object entity) {
        return ((org.springframework.util.MultiValueMap) collection).containsValue(entity);
    }

    @Override
    public Object indexOf(Object collection, Object entity) {
        for (Iterator i = ((org.springframework.util.MultiValueMap) collection).entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            Collection value = (Collection) entry.getValue();
            if (value.contains(entity)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Object replaceElements(Object original, Object target, CollectionPersister persister, Object owner, Map copyCache, SharedSessionContractImplementor session) throws HibernateException {
        org.springframework.util.MultiValueMap  result = (org.springframework.util.MultiValueMap) target;
        result.clear();
        Iterator iter = ((Map) original).entrySet().iterator();
        while (iter.hasNext()) {
            java.util.Map.Entry me = (java.util.Map.Entry) iter.next();
            Object key = persister.getIndexType().replace( me.getKey(), null, session, owner, copyCache );
            Collection collection = (Collection) me.getValue();
            for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
                Object value = persister.getElementType().replace( iterator.next(), null, session, owner, copyCache );
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return new LinkedMultiValueMap<K, V>();
    }
}

