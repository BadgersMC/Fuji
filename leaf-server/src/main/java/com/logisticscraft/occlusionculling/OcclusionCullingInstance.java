1|1|package com.logisticscraft.occlusionculling;
2|2|
3|3|import com.logisticscraft.occlusionculling.cache.ArrayOcclusionCache;
4|4|import com.logisticscraft.occlusionculling.cache.OcclusionCache;
5|5|import com.logisticscraft.occlusionculling.util.MathUtilities;
6|6|import com.logisticscraft.occlusionculling.util.Vec3d;
7|7|
8|8|import java.util.Arrays;
9|9|import java.util.BitSet;
10|10|import java.util.HashMap;
11|11|import java.util.Map;
12|12|
13|13|import org.slf4j.Logger;
14|14|import org.slf4j.LoggerFactory;
15|15|
16|16|public class OcclusionCullingInstance {
17|17|
18|18|    private static final int ON_MIN_X = 0x01;
19|19|    private static final int ON_MAX_X = 0x02;
20|20|    private static final int ON_MIN_Y = 0x04;
21|21|    private static final int ON_MAX_Y = 0x08;
22|22|    private static final int ON_MIN_Z = 0x10;
23|23|    private static final int ON_MAX_Z = 0x20;
24|24|
25|25|    private final int reach;
26|26|    private final double aabbExpansion;
27|27|    private final DataProvider provider;
28|28|    private final OcclusionCache cache;
29|29|
30|30|    private static final Logger LOGGER = LoggerFactory.getLogger(OcclusionCullingInstance.class);
31|31|    private static final Map<String, Long> lastErrorLogged = new HashMap<>();
32|32|    private static final long ERROR_LOG_COOLDOWN_MS = 5000;
33|33|
34|34|    // Reused allocated data structures
35|35|    private final BitSet skipList = new BitSet(); // Grows bigger in case some mod introduces giant hitboxes
36|36|    private final Vec3d[] targetPoints = new Vec3d[15];
37|37|    private Vec3d targetPos = new Vec3d(0, 0, 0);
38|38|    private final int[] cameraPos = new int[3];
39|39|    private final boolean[] dotselectors = new boolean[14];
40|40|    private boolean allowRayChecks = false;
41|41|    private final int[] lastHitBlock = new int[3];
42|42|    private boolean allowWallClipping = false;
43|43|
44|44|
45|45|    public OcclusionCullingInstance(int maxDistance, DataProvider provider) {
46|46|        this(maxDistance, provider, new ArrayOcclusionCache(maxDistance), org.dreeam.leaf.config.modules.misc.RaytraceTracker.boundingBoxExpansion);
47|47|    }
48|48|
49|49|    public OcclusionCullingInstance(int maxDistance, DataProvider provider, OcclusionCache cache, double aabbExpansion) {
50|50|        this.reach = maxDistance;
51|51|        this.provider = provider;
52|52|        this.cache = cache;
53|53|        this.aabbExpansion = aabbExpansion;
54|54|        for (int i = 0; i < targetPoints.length; i++) {
55|55|            targetPoints[i] = new Vec3d(0, 0, 0);
56|56|        }
57|57|    }
58|58|
59|59|    public boolean isAABBVisible(Vec3d aabbMin, Vec3d aabbMax, Vec3d viewerPosition) {
60|60|        try {
61|61|            int maxX = MathUtilities.floor(aabbMax.x()
62|62|                + aabbExpansion);
63|63|            int maxY = MathUtilities.floor(aabbMax.y()
64|64|                + aabbExpansion);
65|65|            int maxZ = MathUtilities.floor(aabbMax.z()
66|66|                + aabbExpansion);
67|67|            int minX = MathUtilities.floor(aabbMin.x()
68|68|                - aabbExpansion);
69|69|            int minY = MathUtilities.floor(aabbMin.y()
70|70|                - aabbExpansion);
71|71|            int minZ = MathUtilities.floor(aabbMin.z()
72|72|                - aabbExpansion);
73|73|
74|74|            cameraPos[0] = MathUtilities.floor(viewerPosition.x());
75|75|            cameraPos[1] = MathUtilities.floor(viewerPosition.y());
76|76|            cameraPos[2] = MathUtilities.floor(viewerPosition.z());
77|77|
78|78|            Relative relX = Relative.from(minX, maxX, cameraPos[0]);
79|79|            Relative relY = Relative.from(minY, maxY, cameraPos[1]);
80|80|            Relative relZ = Relative.from(minZ, maxZ, cameraPos[2]);
81|81|
82|82|            if (relX == Relative.INSIDE && relY == Relative.INSIDE && relZ == Relative.INSIDE) {
83|83|                return true; // We are inside of the AABB, don't cull
84|84|            }
85|85|
86|86|            skipList.clear();
87|87|
88|88|            // Just check the cache first
89|89|            int id = 0;
90|90|            for (int x = minX; x <= maxX; x++) {
91|91|                for (int y = minY; y <= maxY; y++) {
92|92|                    for (int z = minZ; z <= maxZ; z++) {
93|93|                        int cachedValue = getCacheValue(x, y, z);
94|94|
95|95|                        if (cachedValue == 1) {
96|96|                            // non-occluding
97|97|                            return true;
98|98|                        }
99|99|
100|100|                        if (cachedValue != 0) {
101|101|                            // was checked and it wasn't visible
102|102|                            skipList.set(id);
103|103|                        }
104|104|                        id++;
105|105|                    }
106|106|                }
107|107|            }
108|108|
109|109|            // only after the first hit wall the cache becomes valid.
110|110|            allowRayChecks = false;
111|111|
112|112|            // since the cache wasn't helpfull 
113|113|            id = 0;
114|114|            for (int x = minX; x <= maxX; x++) {
115|115|                byte visibleOnFaceX = 0;
116|116|                byte faceEdgeDataX = 0;
117|117|                faceEdgeDataX |= (x == minX) ? ON_MIN_X : 0;
118|118|                faceEdgeDataX |= (x == maxX) ? ON_MAX_X : 0;
119|119|                visibleOnFaceX |= (x == minX && relX == Relative.POSITIVE) ? ON_MIN_X : 0;
120|120|                visibleOnFaceX |= (x == maxX && relX == Relative.NEGATIVE) ? ON_MAX_X : 0;
121|121|                for (int y = minY; y <= maxY; y++) {
122|122|                    byte faceEdgeDataY = faceEdgeDataX;
123|123|                    byte visibleOnFaceY = visibleOnFaceX;
124|124|                    faceEdgeDataY |= (y == minY) ? ON_MIN_Y : 0;
125|125|                    faceEdgeDataY |= (y == maxY) ? ON_MAX_Y : 0;
126|126|                    visibleOnFaceY |= (y == minY && relY == Relative.POSITIVE) ? ON_MIN_Y : 0;
127|127|                    visibleOnFaceY |= (y == maxY && relY == Relative.NEGATIVE) ? ON_MAX_Y : 0;
128|128|                    for (int z = minZ; z <= maxZ; z++) {
129|129|                        byte faceEdgeData = faceEdgeDataY;
130|130|                        byte visibleOnFace = visibleOnFaceY;
131|131|                        faceEdgeData |= (z == minZ) ? ON_MIN_Z : 0;
132|132|                        faceEdgeData |= (z == maxZ) ? ON_MAX_Z : 0;
133|133|                        visibleOnFace |= (z == minZ && relZ == Relative.POSITIVE) ? ON_MIN_Z : 0;
134|134|                        visibleOnFace |= (z == maxZ && relZ == Relative.NEGATIVE) ? ON_MAX_Z : 0;
135|135|                        if (skipList.get(id)) { // was checked and it wasn't visible
136|136|                            id++;
137|137|                            continue;
138|138|                        }
139|139|
140|140|                        if (visibleOnFace != 0) {
141|141|                            targetPos = new Vec3d(x, y, z);
142|142|                            if (isVoxelVisible(viewerPosition, targetPos, faceEdgeData, visibleOnFace)) {
143|143|                                return true;
144|144|                            }
145|145|                        }
146|146|                        id++;
147|147|                    }
148|148|                }
149|149|            }
150|150|
151|151|            return false;
152|152|        } catch (Exception e) {
153|153|            String key = e.getClass().getSimpleName();
154|154|            long now = System.currentTimeMillis();
155|155|            Long last = lastErrorLogged.get(key);
156|156|            if (last == null || now - last >= ERROR_LOG_COOLDOWN_MS) {
157|157|                lastErrorLogged.put(key, now);
158|158|                LOGGER.warn("[OcclusionCulling] {} in isAABBVisible: {}", key, e.getMessage());
159|159|            }
160|160|        }
161|161|        return true;
162|162|    }
163|163|
164|164|    /**
165|165|     * @param viewerPosition
166|166|     * @param position
167|167|     * @param faceData       contains rather this Block is on the outside for a given face
168|168|     * @param visibleOnFace  contains rather a face should be concidered
169|169|     * @return
170|170|     */
171|171|    private boolean isVoxelVisible(Vec3d viewerPosition, Vec3d position, byte faceData, byte visibleOnFace) {
172|172|        int targetSize = 0;
173|173|        Arrays.fill(dotselectors, false);
174|174|        if ((visibleOnFace & ON_MIN_X) == ON_MIN_X) {
175|175|            dotselectors[0] = true;
176|176|            if ((faceData & ~ON_MIN_X) != 0) {
177|177|                dotselectors[1] = true;
178|178|                dotselectors[4] = true;
179|179|                dotselectors[5] = true;
180|180|            }
181|181|            dotselectors[8] = true;
182|182|        }
183|183|        if ((visibleOnFace & ON_MIN_Y) == ON_MIN_Y) {
184|184|            dotselectors[0] = true;
185|185|            if ((faceData & ~ON_MIN_Y) != 0) {
186|186|                dotselectors[3] = true;
187|187|                dotselectors[4] = true;
188|188|                dotselectors[7] = true;
189|189|            }
190|190|            dotselectors[9] = true;
191|191|        }
192|192|        if ((visibleOnFace & ON_MIN_Z) == ON_MIN_Z) {
193|193|            dotselectors[0] = true;
194|194|            if ((faceData & ~ON_MIN_Z) != 0) {
195|195|                dotselectors[1] = true;
196|196|                dotselectors[4] = true;
197|197|                dotselectors[5] = true;
198|198|            }
199|199|            dotselectors[10] = true;
200|200|        }
201|201|        if ((visibleOnFace & ON_MAX_X) == ON_MAX_X) {
202|202|            dotselectors[4] = true;
203|203|            if ((faceData & ~ON_MAX_X) != 0) {
204|204|                dotselectors[5] = true;
205|205|                dotselectors[6] = true;
206|206|                dotselectors[7] = true;
207|207|            }
208|208|            dotselectors[11] = true;
209|209|        }
210|210|        if ((visibleOnFace & ON_MAX_Y) == ON_MAX_Y) {
211|211|            dotselectors[1] = true;
212|212|            if ((faceData & ~ON_MAX_Y) != 0) {
213|213|                dotselectors[2] = true;
214|214|                dotselectors[5] = true;
215|215|                dotselectors[6] = true;
216|216|            }
217|217|            dotselectors[12] = true;
218|218|        }
219|219|        if ((visibleOnFace & ON_MAX_Z) == ON_MAX_Z) {
220|220|            dotselectors[2] = true;
221|221|            if ((faceData & ~ON_MAX_Z) != 0) {
222|222|                dotselectors[3] = true;
223|223|                dotselectors[6] = true;
224|224|                dotselectors[7] = true;
225|225|            }
226|226|            dotselectors[13] = true;
227|227|        }
228|228|
229|229|        if (dotselectors[0]) targetPoints[targetSize++] = position.add(0.05, 0.05, 0.05);
230|230|        if (dotselectors[1]) targetPoints[targetSize++] = position.add(0.05, 0.95, 0.05);
231|231|        if (dotselectors[2]) targetPoints[targetSize++] = position.add(0.05, 0.95, 0.95);
232|232|        if (dotselectors[3]) targetPoints[targetSize++] = position.add(0.05, 0.05, 0.95);
233|233|        if (dotselectors[4]) targetPoints[targetSize++] = position.add(0.95, 0.05, 0.05);
234|234|        if (dotselectors[5]) targetPoints[targetSize++] = position.add(0.95, 0.95, 0.05);
235|235|        if (dotselectors[6]) targetPoints[targetSize++] = position.add(0.95, 0.95, 0.95);
236|236|        if (dotselectors[7]) targetPoints[targetSize++] = position.add(0.95, 0.05, 0.95);
237|237|        // middle points
238|238|        if (dotselectors[8]) targetPoints[targetSize++] = position.add(0.05, 0.5, 0.5);
239|239|        if (dotselectors[9]) targetPoints[targetSize++] = position.add(0.5, 0.05, 0.5);
240|240|        if (dotselectors[10]) targetPoints[targetSize++] = position.add(0.5, 0.5, 0.05);
241|241|        if (dotselectors[11]) targetPoints[targetSize++] = position.add(0.95, 0.5, 0.5);
242|242|        if (dotselectors[12]) targetPoints[targetSize++] = position.add(0.5, 0.95, 0.5);
243|243|        if (dotselectors[13]) targetPoints[targetSize++] = position.add(0.5, 0.5, 0.95);
244|244|
245|245|        return isVisible(viewerPosition, targetPoints, targetSize);
246|246|    }
247|247|
248|248|    private boolean rayIntersection(int[] b, Vec3d rayOrigin, Vec3d rayDir) {
249|249|        Vec3d rInv = new Vec3d(1, 1, 1).div(rayDir);
250|250|
251|251|        double t1 = (b[0] - rayOrigin.x()) * rInv.x();
252|252|        double t2 = (b[0] + 1 - rayOrigin.x()) * rInv.x();
253|253|        double t3 = (b[1] - rayOrigin.y()) * rInv.y();
254|254|        double t4 = (b[1] + 1 - rayOrigin.y()) * rInv.y();
255|255|        double t5 = (b[2] - rayOrigin.z()) * rInv.z();
256|256|        double t6 = (b[2] + 1 - rayOrigin.z()) * rInv.z();
257|257|
258|258|        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
259|259|        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));
260|260|
261|261|        // if tmax < 0, ray (line) is intersecting AABB, but the whole AABB is behind us
262|262|        if (tmax < 0) {
263|263|            return false;
264|264|        }
265|265|
266|266|        // if tmin > tmax, ray doesn't intersect AABB
267|267|        return !(tmin > tmax);
268|268|    }
269|269|
270|270|    /**
271|271|     * returns the grid cells that intersect with this Vec3d<br>
272|272|     * <a href=
273|273|     * "http://playtechs.blogspot.de/2007/03/raytracing-on-grid.html">http://playtechs.blogspot.de/2007/03/raytracing-on-grid.html</a>
274|274|     * <p>
275|275|     * Caching assumes that all Vec3d's are inside the same block
276|276|     */
277|277|    private boolean isVisible(Vec3d start, Vec3d[] targets, int size) {
278|278|        // start cell coordinate
279|279|        int x = cameraPos[0];
280|280|        int y = cameraPos[1];
281|281|        int z = cameraPos[2];
282|282|
283|283|        for (int v = 0; v < size; v++) {
284|284|            // ray-casting target
285|285|            Vec3d target = targets[v];
286|286|
287|287|            double relativeX = start.x() - target.x();
288|288|            double relativeY = start.y() - target.y();
289|289|            double relativeZ = start.z() - target.z();
290|290|
291|291|            if (allowRayChecks && rayIntersection(lastHitBlock, start, new Vec3d(relativeX, relativeY, relativeZ).normalize())) {
292|292|                continue;
293|293|            }
294|294|
295|295|            // horizontal and vertical cell amount spanned
296|296|            double dimensionX = Math.abs(relativeX);
297|297|            double dimensionY = Math.abs(relativeY);
298|298|            double dimensionZ = Math.abs(relativeZ);
299|299|
300|300|            // distance between horizontal intersection points with cell border as a
301|301|            // fraction of the total Vec3d length
302|302|            double dimFracX = 1f / dimensionX;
303|303|            // distance between vertical intersection points with cell border as a fraction
304|304|            // of the total Vec3d length
305|305|            double dimFracY = 1f / dimensionY;
306|306|            double dimFracZ = 1f / dimensionZ;
307|307|
308|308|            // total amount of intersected cells
309|309|            int intersectCount = 1;
310|310|
311|311|            // 1, 0 or -1
312|312|            // determines the direction of the next cell (horizontally / vertically)
313|313|            int x_inc, y_inc, z_inc;
314|314|
315|315|            // the distance to the next horizontal / vertical intersection point with a cell
316|316|            // border as a fraction of the total Vec3d length
317|317|            double t_next_y, t_next_x, t_next_z;
318|318|
319|319|            if (dimensionX == 0f) {
320|320|                x_inc = 0;
321|321|                t_next_x = dimFracX; // don't increment horizontally because the Vec3d is perfectly vertical
322|322|            } else if (target.x() > start.x()) {
323|323|                x_inc = 1; // target point is horizontally greater than starting point so increment every
324|324|                // step by 1
325|325|                intersectCount += MathUtilities.floor(target.x()) - x; // increment total amount of intersecting cells
326|326|                t_next_x = (float) ((x + 1 - start.x()) * dimFracX); // calculate the next horizontal
327|327|                // intersection
328|328|                // point based on the position inside
329|329|                // the first cell
330|330|            } else {
331|331|                x_inc = -1; // target point is horizontally smaller than starting point so reduce every step
332|332|                // by 1
333|333|                intersectCount += x - MathUtilities.floor(target.x()); // increment total amount of intersecting cells
334|334|                t_next_x = (float) ((start.x() - x)
335|335|                    * dimFracX); // calculate the next horizontal
336|336|                // intersection point
337|337|                // based on the position inside
338|338|                // the first cell
339|339|            }
340|340|
341|341|            if (dimensionY == 0f) {
342|342|                y_inc = 0;
343|343|                t_next_y = dimFracY; // don't increment vertically because the Vec3d is perfectly horizontal
344|344|            } else if (target.y() > start.y()) {
345|345|                y_inc = 1; // target point is vertically greater than starting point so increment every
346|346|                // step by 1
347|347|                intersectCount += MathUtilities.floor(target.y()) - y; // increment total amount of intersecting cells
348|348|                t_next_y = (float) ((y + 1 - start.y())
349|349|                    * dimFracY); // calculate the next vertical
350|350|                // intersection
351|351|                // point based on the position inside
352|352|                // the first cell
353|353|            } else {
354|354|                y_inc = -1; // target point is vertically smaller than starting point so reduce every step
355|355|                // by 1
356|356|                intersectCount += y - MathUtilities.floor(target.y()); // increment total amount of intersecting cells
357|357|                t_next_y = (float) ((start.y() - y)
358|358|                    * dimFracY); // calculate the next vertical intersection
359|359|                // point
360|360|                // based on the position inside
361|361|                // the first cell
362|362|            }
363|363|
364|364|            if (dimensionZ == 0f) {
365|365|                z_inc = 0;
366|366|                t_next_z = dimFracZ; // don't increment vertically because the Vec3d is perfectly horizontal
367|367|            } else if (target.z() > start.z()) {
368|368|                z_inc = 1; // target point is vertically greater than starting point so increment every
369|369|                // step by 1
370|370|                intersectCount += MathUtilities.floor(target.z()) - z; // increment total amount of intersecting cells
371|371|                t_next_z = (float) ((z + 1 - start.z())
372|372|                    * dimFracZ); // calculate the next vertical
373|373|                // intersection
374|374|                // point based on the position inside
375|375|                // the first cell
376|376|            } else {
377|377|                z_inc = -1; // target point is vertically smaller than starting point so reduce every step
378|378|                // by 1
379|379|                intersectCount += z - MathUtilities.floor(target.z()); // increment total amount of intersecting cells
380|380|                t_next_z = (float) ((start.z() - z)
381|381|                    * dimFracZ); // calculate the next vertical intersection
382|382|                // point
383|383|                // based on the position inside
384|384|                // the first cell
385|385|            }
386|386|
387|387|            boolean finished = stepRay(start, x, y, z,
388|388|                dimFracX, dimFracY, dimFracZ, intersectCount, x_inc, y_inc,
389|389|                z_inc, t_next_y, t_next_x, t_next_z);
390|390|            provider.cleanup();
391|391|            if (finished) {
392|392|                cacheResult(targets[0], true);
393|393|                return true;
394|394|            } else {
395|395|                allowRayChecks = true;
396|396|            }
397|397|        }
398|398|        cacheResult(targets[0], false);
399|399|        return false;
400|400|    }
401|401|
402|402|    private boolean stepRay(Vec3d start, int currentX, int currentY,
403|403|                            int currentZ, double distInX, double distInY,
404|404|                            double distInZ, int n, int x_inc, int y_inc,
405|405|                            int z_inc, double t_next_y, double t_next_x,
406|406|                            double t_next_z) {
407|407|        allowWallClipping = true; // initially allow rays to go through walls till they are on the outside
408|408|        // iterate through all intersecting cells (n times)
409|409|        for (; n > 1; n--) { // n-1 times because we don't want to check the last block
410|410|            // towards - where from
411|411|
412|412|
413|413|            // get cached value, 0 means uncached (default)
414|414|            int cVal = getCacheValue(currentX, currentY, currentZ);
415|415|
416|416|            if (cVal == 2 && !allowWallClipping) {
417|417|                // block cached as occluding, stop ray
418|418|                lastHitBlock[0] = currentX;
419|419|                lastHitBlock[1] = currentY;
420|420|                lastHitBlock[2] = currentZ;
421|421|                return false;
422|422|            }
423|423|
424|424|            if (cVal == 0) {
425|425|                // save current cell
426|426|                int chunkX = currentX >> 4;
427|427|                int chunkZ = currentZ >> 4;
428|428|
429|429|                if (!provider.prepareChunk(chunkX, chunkZ)) { // Chunk not ready
430|430|                    return false;
431|431|                }
432|432|
433|433|                if (provider.isOpaqueFullCube(currentX, currentY, currentZ)) {
434|434|                    if (!allowWallClipping) {
435|435|                        cache.setLastHidden();
436|436|                        lastHitBlock[0] = currentX;
437|437|                        lastHitBlock[1] = currentY;
438|438|                        lastHitBlock[2] = currentZ;
439|439|                        return false;
440|440|                    }
441|441|                } else {
442|442|                    // outside of wall, now clipping is not allowed
443|443|                    allowWallClipping = false;
444|444|                    cache.setLastVisible();
445|445|                }
446|446|            }
447|447|
448|448|            if (cVal == 1) {
449|449|                // outside of wall, now clipping is not allowed
450|450|                allowWallClipping = false;
451|451|            }
452|452|
453|453|
454|454|            if (t_next_y < t_next_x && t_next_y < t_next_z) { // next cell is upwards/downwards because the distance to
455|455|                // the next vertical
456|456|                // intersection point is smaller than to the next horizontal intersection point
457|457|                currentY += y_inc; // move up/down
458|458|                t_next_y += distInY; // update next vertical intersection point
459|459|            } else if (t_next_x < t_next_y && t_next_x < t_next_z) { // next cell is right/left
460|460|                currentX += x_inc; // move right/left
461|461|                t_next_x += distInX; // update next horizontal intersection point
462|462|            } else {
463|463|                currentZ += z_inc; // move right/left
464|464|                t_next_z += distInZ; // update next horizontal intersection point
465|465|            }
466|466|
467|467|        }
468|468|        return true;
469|469|    }
470|470|
471|471|    // -1 = invalid location, 0 = not checked yet, 1 = visible, 2 = occluding
472|472|    private int getCacheValue(int x, int y, int z) {
473|473|        x -= cameraPos[0];
474|474|        y -= cameraPos[1];
475|475|        z -= cameraPos[2];
476|476|        if (Math.abs(x) > reach - 2 || Math.abs(y) > reach - 2
477|477|            || Math.abs(z) > reach - 2) {
478|478|            return -1;
479|479|        }
480|480|
481|481|        // check if target is already known
482|482|        return cache.getState(x + reach, y + reach, z + reach);
483|483|    }
484|484|
485|485|
486|486|    private void cacheResult(int x, int y, int z, boolean result) {
487|487|        int cx = x - cameraPos[0] + reach;
488|488|        int cy = y - cameraPos[1] + reach;
489|489|        int cz = z - cameraPos[2] + reach;
490|490|        if (result) {
491|491|            cache.setVisible(cx, cy, cz);
492|492|        } else {
493|493|            cache.setHidden(cx, cy, cz);
494|494|        }
495|495|    }
496|496|
497|497|    private void cacheResult(Vec3d vector, boolean result) {
498|498|        int cx = MathUtilities.floor(vector.x()) - cameraPos[0] + reach;
499|499|        int cy = MathUtilities.floor(vector.y()) - cameraPos[1] + reach;
500|500|        int cz = MathUtilities.floor(vector.z()) - cameraPos[2] + reach;
501|