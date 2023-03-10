import moe.jeremie.owl.terminal.ImageProtocol.ImageProtocolKotlin.ImageProtocol

//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: ImageProtocol.proto

@kotlin.jvm.JvmName("-initializeimageResponse")
public inline fun imageResponse(block: ImageResponseKt.Dsl.() -> kotlin.Unit): ImageProtocol.ImageResponse =
  ImageResponseKt.Dsl._create(ImageProtocol.ImageResponse.newBuilder()).apply { block() }._build()
public object ImageResponseKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: ImageProtocol.ImageResponse.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: ImageProtocol.ImageResponse.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): ImageProtocol.ImageResponse = _builder.build()

    /**
     * <code>int32 cmd_id = 1;</code>
     */
    public var cmdId: kotlin.Int
      @JvmName("getCmdId")
      get() = _builder.getCmdId()
      @JvmName("setCmdId")
      set(value) {
        _builder.setCmdId(value)
      }
    /**
     * <code>int32 cmd_id = 1;</code>
     */
    public fun clearCmdId() {
      _builder.clearCmdId()
    }

    /**
     * <code>optional int32 package_id = 2;</code>
     */
    public var packageId: kotlin.Int
      @JvmName("getPackageId")
      get() = _builder.getPackageId()
      @JvmName("setPackageId")
      set(value) {
        _builder.setPackageId(value)
      }
    /**
     * <code>optional int32 package_id = 2;</code>
     */
    public fun clearPackageId() {
      _builder.clearPackageId()
    }
    /**
     * <code>optional int32 package_id = 2;</code>
     * @return Whether the packageId field is set.
     */
    public fun hasPackageId(): kotlin.Boolean {
      return _builder.hasPackageId()
    }

    /**
     * <code>optional int32 camera_id = 3;</code>
     */
    public var cameraId: kotlin.Int
      @JvmName("getCameraId")
      get() = _builder.getCameraId()
      @JvmName("setCameraId")
      set(value) {
        _builder.setCameraId(value)
      }
    /**
     * <code>optional int32 camera_id = 3;</code>
     */
    public fun clearCameraId() {
      _builder.clearCameraId()
    }
    /**
     * <code>optional int32 camera_id = 3;</code>
     * @return Whether the cameraId field is set.
     */
    public fun hasCameraId(): kotlin.Boolean {
      return _builder.hasCameraId()
    }

    /**
     * <code>optional int32 image_width = 11;</code>
     */
    public var imageWidth: kotlin.Int
      @JvmName("getImageWidth")
      get() = _builder.getImageWidth()
      @JvmName("setImageWidth")
      set(value) {
        _builder.setImageWidth(value)
      }
    /**
     * <code>optional int32 image_width = 11;</code>
     */
    public fun clearImageWidth() {
      _builder.clearImageWidth()
    }
    /**
     * <code>optional int32 image_width = 11;</code>
     * @return Whether the imageWidth field is set.
     */
    public fun hasImageWidth(): kotlin.Boolean {
      return _builder.hasImageWidth()
    }

    /**
     * <code>optional int32 image_height = 12;</code>
     */
    public var imageHeight: kotlin.Int
      @JvmName("getImageHeight")
      get() = _builder.getImageHeight()
      @JvmName("setImageHeight")
      set(value) {
        _builder.setImageHeight(value)
      }
    /**
     * <code>optional int32 image_height = 12;</code>
     */
    public fun clearImageHeight() {
      _builder.clearImageHeight()
    }
    /**
     * <code>optional int32 image_height = 12;</code>
     * @return Whether the imageHeight field is set.
     */
    public fun hasImageHeight(): kotlin.Boolean {
      return _builder.hasImageHeight()
    }

    /**
     * <code>optional uint64 image_data_size = 50;</code>
     */
    public var imageDataSize: kotlin.Long
      @JvmName("getImageDataSize")
      get() = _builder.getImageDataSize()
      @JvmName("setImageDataSize")
      set(value) {
        _builder.setImageDataSize(value)
      }
    /**
     * <code>optional uint64 image_data_size = 50;</code>
     */
    public fun clearImageDataSize() {
      _builder.clearImageDataSize()
    }
    /**
     * <code>optional uint64 image_data_size = 50;</code>
     * @return Whether the imageDataSize field is set.
     */
    public fun hasImageDataSize(): kotlin.Boolean {
      return _builder.hasImageDataSize()
    }

    /**
     * <code>optional int32 image_pixel_channel = 51;</code>
     */
    public var imagePixelChannel: kotlin.Int
      @JvmName("getImagePixelChannel")
      get() = _builder.getImagePixelChannel()
      @JvmName("setImagePixelChannel")
      set(value) {
        _builder.setImagePixelChannel(value)
      }
    /**
     * <code>optional int32 image_pixel_channel = 51;</code>
     */
    public fun clearImagePixelChannel() {
      _builder.clearImagePixelChannel()
    }
    /**
     * <code>optional int32 image_pixel_channel = 51;</code>
     * @return Whether the imagePixelChannel field is set.
     */
    public fun hasImagePixelChannel(): kotlin.Boolean {
      return _builder.hasImagePixelChannel()
    }

    /**
     * <code>optional .ImageFormat image_format = 52;</code>
     */
    public var imageFormat: ImageProtocol.ImageFormat
      @JvmName("getImageFormat")
      get() = _builder.getImageFormat()
      @JvmName("setImageFormat")
      set(value) {
        _builder.setImageFormat(value)
      }
    /**
     * <code>optional .ImageFormat image_format = 52;</code>
     */
    public fun clearImageFormat() {
      _builder.clearImageFormat()
    }
    /**
     * <code>optional .ImageFormat image_format = 52;</code>
     * @return Whether the imageFormat field is set.
     */
    public fun hasImageFormat(): kotlin.Boolean {
      return _builder.hasImageFormat()
    }

    /**
     * <code>optional bytes image_data = 60;</code>
     */
    public var imageData: com.google.protobuf.ByteString
      @JvmName("getImageData")
      get() = _builder.getImageData()
      @JvmName("setImageData")
      set(value) {
        _builder.setImageData(value)
      }
    /**
     * <code>optional bytes image_data = 60;</code>
     */
    public fun clearImageData() {
      _builder.clearImageData()
    }
    /**
     * <code>optional bytes image_data = 60;</code>
     * @return Whether the imageData field is set.
     */
    public fun hasImageData(): kotlin.Boolean {
      return _builder.hasImageData()
    }

    /**
     * <code>optional bool is_ok = 101;</code>
     */
    public var isOk: kotlin.Boolean
      @JvmName("getIsOk")
      get() = _builder.getIsOk()
      @JvmName("setIsOk")
      set(value) {
        _builder.setIsOk(value)
      }
    /**
     * <code>optional bool is_ok = 101;</code>
     */
    public fun clearIsOk() {
      _builder.clearIsOk()
    }
    /**
     * <code>optional bool is_ok = 101;</code>
     * @return Whether the isOk field is set.
     */
    public fun hasIsOk(): kotlin.Boolean {
      return _builder.hasIsOk()
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun ImageProtocol.ImageResponse.copy(block: ImageResponseKt.Dsl.() -> kotlin.Unit): ImageProtocol.ImageResponse =
  ImageResponseKt.Dsl._create(this.toBuilder()).apply { block() }._build()

