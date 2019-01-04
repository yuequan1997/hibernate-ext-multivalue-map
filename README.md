# hibernate-ext-multivalue-map

### How to use?
First include dependency
```xml
<dependency>
    <groupId>org.yuequan</groupId>
    <artifactId>hibernate-ext-multivalue-map</artifactId>
    <version>1.0.0.BETA</version>
</dependency>
```
Configure your entity
```java
//... more ...
@OneToMany or @ManyToMany
@CollectionType(type = "org.yuequan.hibernate.ext.usertype.MultiValueMap")
@MapKey or @MapKeyColumn
// .. more ...
private Map<Integer, EntityType> group; // =>  key => List<EntityType>
```
But need to convert when you take the value
```java
((List<EntityTye>) entity.getGroup().get(key))
```