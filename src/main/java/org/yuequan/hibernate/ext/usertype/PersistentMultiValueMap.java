package org.yuequan.hibernate.ext.usertype;

import org.hibernate.HibernateException;
import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.persister.collection.CollectionPersister;
import org.springframework.util.LinkedMultiValueMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PersistentMultiValueMap extends PersistentMap {

    public PersistentMultiValueMap(SharedSessionContractImplementor session) {
        super(session);
    }

    public PersistentMultiValueMap(SharedSessionContractImplementor session, Map map) {
        super(session, map);
    }

    private transient List<Object[]> loadingEntries;

    @Override
    @SuppressWarnings("unchecked")
    public Object readFrom(
            ResultSet rs,
            CollectionPersister persister,
            CollectionAliases descriptor,
            Object owner) throws HibernateException, SQLException {
        final Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() );
        if ( element != null ) {
            final Object index = persister.readIndex( rs, descriptor.getSuffixedIndexAliases(), getSession() );
            if ( loadingEntries == null ) {
                loadingEntries = new ArrayList<>();
            }
            loadingEntries.add( new Object[] { index, element } );
        }
        return element;
    }

    @Override
    public boolean endRead() {
        if ( loadingEntries != null ) {
            for ( Object[] entry : loadingEntries ) {
                ((LinkedMultiValueMap) map).add( entry[0], entry[1] );
            }
            loadingEntries = null;
        }
        return super.afterInitialize();
    }
}
