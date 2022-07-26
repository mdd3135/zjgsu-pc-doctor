import asyncio
from mirai import HTTPAdapter, Mirai
import sys

bot = Mirai(
    qq=811745389, # 改成你的机器人的 QQ 号
    adapter=HTTPAdapter(
        verify_key='yirimirai', host='localhost', port=8081
    )
)

@bot.add_background_task
async def background_task():
    await asyncio.sleep(1)
    print("background")
    await bot.send_group_message(636956662, mesg)
    exit()

mesg = str(sys.argv[1])
bot.run()