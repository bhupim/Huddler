package com.experiments.ai.huddler.model;

import com.experiments.ai.huddler.utils.CompressionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EItem implements Serializable {

    private long itemId;

    private long leaf_categ_id;

    private byte[] gallery_url;

    @JsonIgnore
    private BitSet[] embedding;

    @JsonIgnore
    private Map<Long, Integer> hammingDistanceMap = new ConcurrentHashMap<>();

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getLeaf_categ_id() {
        return leaf_categ_id;
    }

    public void setLeaf_categ_id(long leaf_categ_id) {
        this.leaf_categ_id = leaf_categ_id;
    }

    @JsonIgnore
    public byte[] getGallery_url() {
        return gallery_url;
    }

    public void setGallery_url(String gallery_url) {
        byte[] urlCompressed = gallery_url.getBytes();
        try {
            urlCompressed= CompressionUtils.compress(gallery_url.getBytes());
        } catch (Exception ex) {}
        this.gallery_url = urlCompressed;
    }

    public String getGalleryUrl() {
        String url = "";
        try {
            url = new String(CompressionUtils.decompress(gallery_url));
        } catch (Exception ex) { }

        return url;
    }

    public BitSet[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(BitSet[] embedding) {
        this.embedding = embedding;
    }

    public Map<Long, Integer> getHammingDistanceMap() {
        return hammingDistanceMap;
    }

    public void setHammingDistanceMap(Map<Long, Integer> hammingDistanceMap) {
        this.hammingDistanceMap = hammingDistanceMap;
    }

    @Override
    public String toString() {
        return "EItem{" +
                "itemId=" + itemId +
                ", leaf_categ_id=" + leaf_categ_id +
                ", gallery_url='" + gallery_url + '\'' +
                ", embedding=" + embedding +
                '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Long.hashCode(itemId);
        hash = 31 * hash + (gallery_url == null ? 0 : gallery_url.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof EItem))
            return false;

        if (obj == this)
            return true;

        return this.itemId == ((EItem) obj).itemId;
    }

}

