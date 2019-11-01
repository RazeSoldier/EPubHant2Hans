/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */

package razesoldier.epub.reader;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class EPUBBook {
    private String bookName;
    private String lang;
    private Map<String, String> metadata;
    private Map<String, Manifest> manifests;
    private Spines spines;
    private NCX ncx;

    @Contract(pure = true)
    EPUBBook() {
    }

    void setMetadata(@NotNull String bookName, @NotNull String lang, @NotNull Map<String, String> metadata) {
        this.bookName = bookName;
        this.lang = lang;
        this.metadata = metadata;
    }

    void setManifests(@NotNull Map<String, Manifest> manifests) {
        this.manifests = manifests;
    }

    void setSpines(@NotNull Spines spines) {
        this.spines = spines;
    }

    void setNcx(@NotNull NCX ncx) {
        this.ncx = ncx;
    }

    @NotNull
    public String getBookName() {
        return bookName;
    }

    @NotNull
    public String getLanguage() {
        return lang;
    }

    @NotNull
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Get manifests of this book.
     *
     * @return A map, the key is the id of a manifest, the value is a {@link Manifest} instance.
     */
    @NotNull
    public Map<String, Manifest> getManifests() {
        return manifests;
    }

    @NotNull
    public Spines getSpines() {
        return spines;
    }

    @NotNull
    public NCX getNcx() {
        return ncx;
    }

    public static class Manifest {
        private String id;
        private String filePath;
        private String mediaType;

        @Contract(pure = true)
        Manifest(@NotNull String id, @NotNull String filePath, @NotNull String mediaType) {
            this.id = id;
            this.filePath = filePath;
            this.mediaType = mediaType;
        }

        @NotNull
        public String getId() {
            return id;
        }

        @NotNull
        public String getFilePath() {
            return filePath;
        }

        @NotNull
        public String getMediaType() {
            return mediaType;
        }
    }

    public static class Spines {
        private List<String> spineList;
        private String spineFilePath;

        @Contract(pure = true)
        Spines(@NotNull String spineFilePath, @NotNull List<String> spineList) {
            this.spineFilePath = spineFilePath;
            this.spineList = spineList;
        }

        @NotNull
        public List<String> getSpineList() {
            return spineList;
        }

        @NotNull
        public String getSpineFilePath() {
            return spineFilePath;
        }
    }

    public static class NCX {
        private String title;
        private String author;
        private Map<String, String> metadata;
        private Map<String, EPUBBook.NCX.NavPoint> navPointMap;

        void setMetadata(@NotNull Map<String, String> metadata) {
            this.metadata = metadata;
        }

        void setTitle(@NotNull String title) {
            this.title = title;
        }

        void setAuthor(@NotNull String author) {
            this.author = author;
        }

        void setNavPointMap(@NotNull Map<String, EPUBBook.NCX.NavPoint> navPointMap) {
            this.navPointMap = navPointMap;
        }

        @Nullable
        public String getTitle() {
            return title;
        }

        @Nullable
        public String getAuthor() {
            return author;
        }

        @NotNull
        public Map<String, String> getMetadata() {
            return metadata;
        }

        @NotNull
        public Map<String, NavPoint> getNavPointMap() {
            return navPointMap;
        }

        public static class NavPoint {
            private Integer order;
            private String id;
            private String text;
            private String contentRef;

            @Contract(pure = true)
            NavPoint(@NotNull Integer order, @NotNull String id, @NotNull String text, @NotNull String contentRef) {
                this.order = order;
                this.id = id;
                this.text = text;
                this.contentRef = contentRef;
            }

            @NotNull
            public Integer getOrder() {
                return order;
            }

            @NotNull
            public String getId() {
                return id;
            }

            @NotNull
            public String getText() {
                return text;
            }

            @NotNull
            public String getContentRef() {
                return contentRef;
            }
        }
    }
}
