package graphics.scenery.tests.examples.advanced

import cleargl.GLVector
import graphics.scenery.*
import graphics.scenery.backends.Renderer
import graphics.scenery.numerics.Random
import graphics.scenery.tests.examples.basic.TexturedCubeExample
import graphics.scenery.textures.Texture
import graphics.scenery.utils.Image
import org.junit.Test
import java.io.File
import kotlin.concurrent.thread
import kotlin.test.assertTrue

/**
 * Example to show programmatic video recording.
 *
 * @author Ulrik Günther <hello@ulrik.is>
 */
class VideoRecordingExample: SceneryBase("VideoRecordingExample") {
    override fun init() {
        renderer = hub.add(SceneryElement.Renderer,
            Renderer.createRenderer(hub, applicationName, scene, 512, 512))

        val box = Box(GLVector(1.0f, 1.0f, 1.0f))
        with(box) {
            box.name = "le box du win"
            box.material.textures["diffuse"] = Texture.fromImage(Image.fromResource("textures/helix.png", this::class.java))
            scene.addChild(this)
        }

        val light = PointLight(radius = 15.0f)
        light.position = GLVector(0.0f, 0.0f, 2.0f)
        light.intensity = 5.0f
        light.emissionColor = GLVector(1.0f, 1.0f, 1.0f)
        scene.addChild(light)

        val cam: Camera = DetachedHeadCamera()
        with(cam) {
            position = GLVector(0.0f, 0.0f, 5.0f)
            perspectiveCamera(50.0f, 512.0f, 512.0f)
            active = true

            scene.addChild(this)
        }

        thread {
            while (true) {
                box.rotation.rotateByAngleY(Random.randomFromRange(-0.04f, 0.04f))
                box.rotation.rotateByAngleZ(Random.randomFromRange(-0.04f, 0.04f))
                box.needsUpdate = true

                Thread.sleep(20)
            }
        }

        thread {
            while(renderer?.firstImageReady == false) {
                Thread.sleep(50)
            }

            renderer?.recordMovie("./VideoRecordingExample.mp4")
            Thread.sleep(5000)
            renderer?.recordMovie()
        }
    }

    @Test override fun main() {
        // add assertions, these only get called when the example is called
        // as part of scenery's integration tests
        assertions[AssertionCheckPoint.AfterClose]?.add {
            val f = File("./VideoRecordingExample.mp4")
            try {
                assertTrue(f.length() > 0, "Size of recorded video is larger than zero.")
            } finally {
                if(f.exists()) {
                    f.delete()
                }
            }
        }

        super.main()
    }
}
