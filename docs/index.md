--- 
hide: -navigation -toc 
---

# Compose Unstyled

Unstyled, fully accessible components for Jetpack Compose & Compose Multiplatform that you can customize to your heart's content.

Available for 🖥️ Desktop, 🌐 Web (Js/WASM), 🤖 Android, 🍎 iOS, and any other platform Compose can run on.

<style>
  img {
    border-radius: 20px;
  }
  .image-grid {
    margin-top: 80px;
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: 20px;
    text-align: center;
  }
  @media (max-width: 900px) {
    .image-grid {
      grid-template-columns: 1fr 1fr;
    }
  }

  @media (max-width: 600px) {
    .image-grid {
      grid-template-columns: 1fr;
    }
  }
</style>

<div class="image-grid">
    <div>
        <a href="bottom-sheet">
          <img src="preview_bottom_sheet.png" alt="Bottom Sheet Preview">
        </a>
        <div>Bottom Sheet</div>
    </div>

    <div>
        <a href="modal-bottom-sheet">
          <img src="preview_modal_bottom_sheet.png" alt="Bottom Sheet (Modal) Preview">
        </a>
        <div>Bottom Sheet (Modal)</div>
    </div>

  <div>
    <a href="dialog">
      <img src="preview_dialog.png" alt="Dialog Preview">
    </a>
    <div>Dialog</div>
  </div>

  <div>
    <a href="dropdown-menu">
      <img src="preview_menu.png" alt="Menu Preview">
    </a>
    <div>Dropdown Menu</div>
  </div>

  <div>
    <a href="icon">
      <img src="preview_icon.png" alt="Icon Preview">
    </a>
    <div>Icon</div>
  </div>

  <div>
      <a href="scrollarea">
        <img src="preview_scroll_area.png" alt="Scroll Area Preview">
      </a>
      <div>Scroll Area</div>
  </div>

  <div>
      <a href="separators">
        <img src="preview_separators.png" alt="Separators Preview">
      </a>
      <div>Separators</div>
  </div>
</div>

## What developers say

<div class="testimonial-container">
  <div class="testimonial-image">
    <img src="/testimonials/matt.png" alt="Matt Kula">
  </div>
  <div class="testimonial-content">
    <div class="testimonial-text">
      "@alexstyl just wanted to let you know that we just transition from M3 Modal Bottom Sheet to your Compose Unstyled one and <a class="highlight" href="/modal-bottom-sheet">it fixed multiple issues 🎉</a>"
    </div>
    <div class="testimonial-author">
      <h3>Matt Kula</h3>
      <p>Android @ Warner Music Group</p>
    </div>
  </div>
</div>

<div class="testimonial-container">
  <div class="testimonial-image">
    <img src="/testimonials/jacob.jpeg" alt="Jacob Ras">
  </div>
  <div class="testimonial-content">
    <div class="testimonial-text">
"Ever since Compose Unstyled was pointed out to me I use that one.
<a class="highlight" href="/modal-bottom-sheet">Simpler API, and it actually works.</a> I like it a lot, after continuously having something broken with the (Material Compose) bottom sheets."  
    </div>
    <div class="testimonial-author">
      <h3>Jacob Ras</h3>
      <p>Android Engineer @ Albert Heijn</p>
    </div>
  </div>
</div>

<div class="testimonial-container">
  <div class="testimonial-image">
    <img src="/testimonials/gabor.jpg" alt="Gabor Varadi">
  </div>
  <div class="testimonial-content">
    <div class="testimonial-text">
"This man did, what Googlers couldn't in 3 years of <a class="highlight" href="/scrollarea">scrollbars</a> "being on the roadmap" #androiddev"  
    </div>
    <div class="testimonial-author">
      <h3>Jacob Ras</h3>
      <p>EpicPandaForce @ SO</p>
    </div>
  </div>
</div>

<div class="testimonial-container">
  <div class="testimonial-image">
    <img src="/testimonials/alexstyl.jpg" alt="Alex Styl">
  </div>
  <div class="testimonial-content">
    <div class="testimonial-text">
      "I wrote this library, so this will be biased 😁. I was tired of dealing with Material Compose sheets and dialogs issues, <a class="highlight" href="/bottom-sheet">so I decided</a> <a class="highlight" href="/modal-bottom-sheet">to write my own</a> <a class="highlight" href="/dialog">from scratch</a>.
Plus, I needed high-quality, non-Material looking components for my Compose Multiplatform (desktop) apps, hence this library was born."
    </div>
    <div class="testimonial-author">
      <h3>Alex Styl</h3>
      <p>Author of Compose Unstyled</p>
    </div>
  </div>
</div>

<style>
     .highlight {
        background: #fce74c; /* Softer, nicer yellow */
        padding: 0 2px; /* Adds some space around the text */
        border-radius: 3px; /* Slightly rounds the corners */
        box-shadow: 0 0 5px rgba(252, 231, 76, 0.5); /* Creates a glowing effect */
        color: black; /* Sets the text color to black */
        font-weight: bold; /* Optional: makes the text bolder for emphasis */
    }
    
    .highlight a {
        color: black; /* Sets the link text color to black */
        text-decoration: none; /* Optional: removes underline from link */
    }
  .testimonial-container {
    display: flex;
    max-width: 800px;
    font-family: Arial, sans-serif;
    padding-top: 8px;
    padding-bottom: 8px;
    flex-wrap: wrap;
  }
  .testimonial-image {
    flex: 0 0 192px;
    max-width: 192px;
    height: 192px;
  }
  .testimonial-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 10px; /* Rounded corners for the image */
  }
  .testimonial-content {
    flex: 1;
    padding: 20px;
    display: flex;
    flex-direction: column;
  }
  .testimonial-text {
    font-size: 18px;
    margin: 0 0 20px 0;
    line-height: 1.6;
  }
  .testimonial-author h3 {
    margin: 0;
    font-size: 20px;
  }
  .testimonial-author p {
    margin: 5px 0 0;
    font-size: 16px;
    opacity: 0.8;
  }

  @media (max-width: 768px) {
    .testimonial-container {
      flex-direction: column;
      align-items: center;
    }
    .testimonial-image {
      max-width: 100px;
      height: 100px;
      flex: 0 0 100px;
    }
    .testimonial-content {
      padding: 10px;
    }
    .testimonial-text {
      font-size: 16px;
    }
    .testimonial-author h3 {
      font-size: 18px;
    }
    .testimonial-author p {
      font-size: 14px;
    }
  }

  :root {
    --background-color: #ffffff;
    --text-color: #000000;
  }
  @media (prefers-color-scheme: dark) {
    :root {
      --background-color: #1a1a2e;
      --text-color: #ffffff;
    }
  }
</style>
