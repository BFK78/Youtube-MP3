import subprocess
import yt_dlp


def extract_video_info(url):
    ydl_opts = {
        'format': 'best',
        'quiet': True,
        'simulate': True,
        'dump_single_json': True,
        'download': True  # Enable video downloading
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        try:
            video_info = ydl.extract_info(url)
            video_title = video_info.get('title')
            video_thumbnails = video_info.get('thumbnails')
            view_count = video_info.get('view_count')
            like_count = video_info.get('like_count')
            video_url = video_info.get('url')

            # Get the URL of the thumbnail with the highest resolution
            highest_resolution_thumbnail = max(video_thumbnails,
                                               key=lambda t: t.get('width', 0) * t.get('height', 0))
            thumbnail_url = highest_resolution_thumbnail.get('url')

            return {
                'video_title': video_title,
                'thumbnail_url': thumbnail_url,
                'view_count': view_count,
                'like_count': like_count,
                "video_url": video_url,
                # Include the downloaded video file path in the returned dictionary
            }
        except yt_dlp.DownloadError:
            return None


def convert_video_to_mp3(video_file_path):
    output_file_path = video_file_path.replace('.mp4', '.mp3')  # Replace the extension with .mp3

    try:
        # Run the ffmpeg command to convert the video to MP3
        subprocess.run(
            ['ffmpeg', '-i', video_file_path, '-vn', '-acodec', 'libmp3lame', '-q:a', '2',
             output_file_path], check=True)

        return output_file_path  # Return the path of the converted MP3 file
    except subprocess.CalledProcessError:
        return None
